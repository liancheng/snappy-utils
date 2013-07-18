Snappy Command Line Utility
===========================

Snappy-utils is a command line utility tool for the Google Snappy compression library.  It's a simple wrapper around the hadoop-snappy_ library.

__ https://code.google.com/p/hadoop-snappy/

How to install
--------------

Checkout, build and install snappy_::

    $ svn checkout http://snappy.googlecode.com/svn/trunk/ $SNAPPY_HOME
    $ cd $SNAPPY_HOME
    $ ./autogen.sh
    $ ./configure --prefix=$SNAPPY_PREFIX && make
    $ sudo make install

If the ``--prefix`` option is omitted, the default value of ``$SNAPPY_PREFIX`` is ``/usr/local``.

Checkout, build and install hadoop-snappy_::

    $ svn checkout http://hadoop-snappy.googlecode.com/svn/trunk/ $HADOOP_SNAPPY_HOME
    $ cd $HADOOP_SNAPPY_HOME
    $ mvn package -Dsnappy.prefix=$SNAPPY_PREFIX

.. note:: If Maven complains about ``-ljvm`` while building the native hadoop-snappy library, create a symbolic link to your ``libjvm.so`` under ``/usr/lib``.  Usually, you may find ``libjvm.so`` in some subdirectory under ``$JAVA_HOME/jre/lib/``, e.g. ``/usr/lib/jvm/jdk1.7.0_25/jre/lib/amd64/server/libjvm.so``.

Checkout snappy-utils::

    $ git clone git@github.com:liancheng/snappy-utils.git $SNAPPY_UTILS_HOME
    $ cd $SNAPPY_UTILS_HOME

Copy the hadoop-snappy native library files to ``$SNAPPY_UTILS_HOME/lib``::

    $ cp -r $HADOOP_SNAPPY_HOME/target/hadoop-snappy-0.0.1-SNAPSHOT-tar/hadoop-snappy-0.0.1-SNAPSHOT/lib/* $SNAPPY_UTILS_HOME/lib

Build snappy-utils::

    $ sbt package

Create a symbolic link to ``$SNAPPY_UTILS_HOME/bin/snappy`` under one of your ``$PATH`` directory::

    $ sudo ln -sf $SNAPPY_UTILS_HOME/bin/snappy /usr/bin

Usage
-----

Run ``snappy`` without any arguments to check the usage description::

    $ snappy
    Usage: snappy [<options>] <input-file>

       -o, --output        Output file name.

       -d, --decompression Decompression mode.  If the `--output' option is
                           omitted and the input file name ends with
                           `.snappy', the default output file name is the
                           input file name without the `.snappy' suffix.

       -c, --compression   Compression mode, enabled by default.  If the
                           `--output' option is omitted, the default
                           output file name is the   input file name with a
                           `.snappy' suffix.

Samples:

*   Compress ``data.txt`` to ``data.txt.snappy``::

        $ snappy -c data.txt

    Or simply::

        $ snappy data.txt

*   Compress ``data.txt`` to ``compressed-data``::

        $ snappy -c data.txt -o compression-data

    or::

        $ snappy -c data.txt -o compression-data

*   Decompress ``data.txt.snappy`` to ``data.txt``::

        $ snappy -d data.txt.snappy

*   Decompress ``compression-data`` to ``data.txt``::

        $ snappy -d compressed-data -o data.txt

.. _snappy: https://code.google.com/p/hadoop-snappy/
.. _hadoop-snappy: http://code.google.com/p/snappy/
