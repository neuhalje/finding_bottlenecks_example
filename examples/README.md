The following flame chartes where captured on a Fedora 23 VM Ware Fusion running on a MacBook Pro. Sampling started 10 seconds after the java process was launched and lasted 20 seconds. Java bytecode is tinted green. Everything else (including kernel) red.

For an introduction into this kind of graph look at [Brendans site](https://github.com/brendangregg/FlameGraph).


## Unbuffered
[![unbuffered](unbuffered.png)](unbuffered.svg)

This chart had been recorded in _unbuffered_ mode. It is very easy to see, that most time is spent in a kernel functions called by `FileOutputStream::write` (`entry_SYSCALL_...`).  This is not necessarily bad -- after all, copying files needs the kernel for all I/O. This clue needs to be combined with other observations like the excessively long runtime.

In a normal "business" application you want to see your business logic to be executed (that is: the very top of the flames). For a file copy application this _might_ actually be the kernel writing data to the disk. Intuition says something else though: a copy program should be I/O bound, not CPU bound. Something is fishy here!

## Buffered
[![buffered](buffered.png)](buffered.svg)

This chart had been recorded in _buffered_ mode.  Most CPU time is spent in `Copy::copyWriteBytes` (the "business logic").

Both bars of `Copy::copyWriteBytes` (buffered and unbuffered) are more or less the same length. This is easily explained: The sample length is 20 seconds and both versions need longer than 20 seconds to copy the whole file. The buffered version is much more efficient though.

## Copy in blocks
[![in blocks](in_blocks.png)](in_blocks.svg)

This chart had been recorded in _blocks_ mode.  Data is not written into a buffered stream and then flushed to disk but instead is directly copied as blocks.

The most prominet feature of this chart is: It looks a bit like the _unbuffered_ chart. But performance wise this implemantaion is magnitudes faster than the unbuffered versions.

Closer examination shows that roughly 60% of the stacks show that the CPU is idling (`default_idle` on the right side in the middle of the stack).  In other words: in contrast to the unbuffered variant we are not CPU bound.
