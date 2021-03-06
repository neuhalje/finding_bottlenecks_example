#!/bin/bash
# License: WTFPL

function build() {
        # build

        if [ ! -f ./build/libs/synthetic-io-emulator-1.0.1-all.jar ]
          then
          ./gradlew shadowJar
        fi
}

create_java_maps() {
        pushd .

        my_dir="$PWD"

	# jmaps - creates java /tmp/perf-PID.map symbol maps for all java processes.
	#
	# This is a helper script that finds all running "java" processes, then executes
	# perf-map-agent on them all, creating symbol map files in /tmp. These map files
	# are read by perf_events (aka "perf") when doing system profiles (specifically,
	# the "report" and "script" subcommands).
	#
	# My typical workflow is this:
	#
	# perf record -F 99 -a -g -- sleep 30; jmaps
	# perf script > out.stacks
	# ./stackcollapse-perf.pl out.stacks | ./flamegraph.pl --color=java --hash > out.stacks.svg
	#
	# The stackcollapse-perf.pl and flamegraph.pl programs come from:
	# https://github.com/brendangregg/FlameGraph
	#
	# REQUIREMENTS:
	# Tune two environment settings below.
	#
	# 13-Feb-2015	Brendan Gregg	Created this.

	AGENT_HOME=$my_dir/perf-map-agent	# from https://github.com/jrudolph/perf-map-agent

	if [[ "$USER" != root ]]; then
		echo "ERROR: not root user? exiting..."
		exit
	fi

	if [[ ! -x $JAVA_HOME ]]; then
		echo "ERROR: JAVA_HOME not set correctly; edit $0 and fix"
		exit
	fi

	if [[ ! -x $AGENT_HOME ]]; then
		echo "ERROR: AGENT_HOME not set correctly; edit $0 and fix"
		exit
	fi

	# figure out where the agent files are:
	AGENT_OUT=""
	AGENT_JAR=""
	if [[ -e $AGENT_HOME/out/attach-main.jar ]]; then
		AGENT_JAR=$AGENT_HOME/out/attach-main.jar
	elif [[ -e $AGENT_HOME/attach-main.jar ]]; then
		AGENT_JAR=$AGENT_HOME/attach-main.jar
	fi
	if [[ -e $AGENT_HOME/out/libperfmap.so ]]; then
		AGENT_OUT=$AGENT_HOME/out
	elif [[ -e $AGENT_HOME/libperfmap.so ]]; then
		AGENT_OUT=$AGENT_HOME
	fi
	if [[ "$AGENT_OUT" == "" || "$AGENT_JAR" == "" ]]; then
		echo "ERROR: Missing perf-map-agent files in $AGENT_HOME. Check installation."
		exit
	fi

	echo "Fetching maps for all java processes..."
	for pid in $(pgrep -x java); do
		mapfile=/tmp/perf-$pid.map
		[[ -e $mapfile ]] && rm $mapfile
		cmd="cd $AGENT_OUT; $JAVA_HOME/bin/java -Xms32m -Xmx128m -cp $AGENT_JAR:$JAVA_HOME/lib/tools.jar net.virtualvoid.perf.AttachOnce $pid"
		echo $cmd
		user=$(ps ho user -p $pid)
		if [[ "$user" != root ]]; then
			# make $user the username if it is a UID:
			if [[ "$user" == [0-9]* ]]; then user=$(awk -F: '$3 == '$user' { print $1 }' /etc/passwd); fi
			cmd="su - $user -c '$cmd'"
		fi
		echo "Mapping PID $pid (user $user):"
		time eval $cmd
		if [[ -e "$mapfile" ]]; then
			chown root $mapfile
			chmod 666 $mapfile
		else
			echo "ERROR: $mapfile not created."
		fi
		echo
		echo "wc(1): $(wc $mapfile)"
		echo
	done

        popd
}


function run() {
        local mode=$1
        local src="$2"
        local dst="$3"
        local svg="$4"

        # -XX:+PreserveFramePointer is needed, else the code does a terrible job finding the function names for the flamechart
        java -XX:+PreserveFramePointer  -jar ./build/libs/synthetic-io-emulator-1.0.1-all.jar \
           "$mode" \
           "$src" "$dst" &

        PID=$!

        sleep 10 # Warm up

        perf record -F 99 -a -g -- sleep 20; 

        create_java_maps

        perf script > out.stacks
        ./FlameGraph/stackcollapse-perf.pl out.stacks | ./FlameGraph/flamegraph.pl --color=java --hash > "$svg"
        rm -f out.stacks

        # Wait for copy to finish 
        kill $PID #-- it is no longer needed
        wait $PID
}

function verify() {
        local src="$2"
        local dst="$3"

        # verify
        diff "$2" "$3" >/dev/null
        [ $? -eq 0 ] || echo ERROR: The files are not identical!
}

if [ "x$4" == "x" ]
then
  echo "$0 [in_blocks|buffered|unbuffered] sourceFile destFile flamegraph.svg"
  exit 1
fi

build
run $*
# no verify -- the process is killed after sampling
# verify $*

