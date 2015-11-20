About
======

This repository serves two purposes:
- Demonstrate the impact of buffering on write performance (the more or less synthetic problem at hand)
- Demonstrate how to measure system resource usage to *pinpoint the bottleneck* with
 - simple but effective methods like  [USE](http://www.brendangregg.com/usemethod.html) (Utilisation, Saturation, Errors).
 - more advanced tools like [flame graphs](https://github.com/brendangregg/FlameGraph).
 - kernel level tracing like [SystemTap](https://www.sourceware.org/systemtap) to analyse system calls.

Modes to copy
==============
The tool supports three modes to copy data
* *unbuffered* -- Copy data between streams byte-by-byte. This is extremly slow, especially on the write side because each byte is transferred to the kernel in a call to `write`.
* *buffered* -- Copy data between streams byte-by-byte but batch calls to the kernel by using a (8 KiB) buffered output stream.
* *in_blocks* -- Explicitely read and write blocks (8 KiB). The fastest implementation.

build
=======

`./gradlew  shadowJar`

Tools
=========

copy.sh
-----------

* `copy.sh [unbuffered|buffered|in_blocks] SOURCEFILE DESTFILE` -- Useful for performance measurements.
* `copy_count_syscalls.sh [unbuffered|buffered|in_blocks] SOURCEFILE DESTFILE` -- like above but uses SystemTap to count syscalls (needs sudo and the systemtap packages installed).

SystemTap scripts
----------------

[SystemTap](https://www.sourceware.org/systemtap) gives intimate access to Linux kernel internals (basically like Solaris DTrace). In this demo two scripts where used to compare syscall counts in buffered vs. unbuffered variants.

* `util/syscall_count_by_process.stp` - Counts the number of syscalls per process name. Can be used to get an idea if your implementation issues substantial more/less syscalls than other processes (e.g. a C  binary)
* `util/topsys_per_process.stp` -  prints out the  which systemcalls a process used how often (top 20)  


### Installing SystemTap

This is not quite scope of the demo. Visit [this page](https://sourceware.org/systemtap/getinvolved.html) for installation and the [beginners guide](https://www.sourceware.org/systemtap/SystemTap_Beginners_Guide) for first steps.

Debug Symbols
--------------

Install the java debug symbols on Fedora: `dnf install --enablerepo=fedora-debuginfo java-1.8.0-openjdk-debuginfo`.

Performance Results
===================

__ Task: fill this out yourself __

| mode                   | source file size   | duration | throughput|write IO/s (1)| write syscalls (2)   | userland (s) | kernel (s) |
|------------------------|-------------------:|---------:|----------:|-------------:|---------------------:|-------------:|-----------:|
| No output buffering    |  50 MB             |          |           |              |                      |              |            |
| 8 KiB output buffering |  50 MB             |          |           |              |                      |              |            |
| 8 KiB blocks           |  50 MB             |          |           |              |                      |              |            |


LICENSE
-----------

[WTFPL](http://www.wtfpl.net/about/)
