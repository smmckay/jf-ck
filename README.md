# jf-ck

A compiler and interpreter for Brainf-ck. Due to the JVM's widely recognized JIT capabilities, Brainf-ck code compiled with jf-ck is likely to be [as fast as C](http://c2.com/cgi/wiki?AsFastAsCee) in some situations. Even if your code happens to be a smidge slower than C, this is typically acceptable as writing anything significant in Brainf-ck is nigh impossible.

## Brainf-ck Implementation Specifics

* Cells are 8 bit, wrapping
* 2<sup>31</sup> - 1 cells, starting from 0
* `,` returns 0 on EOF

## Running

1. Clone the repo.
2. `./gradlew shadowJar`
3. `java -jar build/libs/jf-ck-1.0-SNAPSHOT-all.jar [-i] <filename>`

jf-ck defaults to compilation. The input is a single file, or stdin if the filename is `-`. If you are cracked in the head and want to compile a file named `-`, maybe try `java -jar build/libs/jf-ck-1.0-SNAPSHOT-all.jar - < -`. The output is a single class file, named `aout.class`, written to the current directory.

The option `-i` causes jf-ck to enter *interpretive mode*, which is quite sophisticated and employs an anonymous inner classloader. The author is quite proud of what he did there, both the hack and the sort-of-punny name for it.

## Using jf-ck as a library

Yes, you can do this. `byte[] us.abbies.b.jfck.Compiler.compile(String)` will compile Brainf-ck code into class bytes. Loading them is the caller's problem; an *anonymous inner classloader* may be useful here. The compiled class implements `Runnable` and that's all you can do with it.

## Justifications For This Project's Existence

None are known.
