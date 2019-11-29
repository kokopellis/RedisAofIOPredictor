#!/bin/bash
while true
do
#dd if=/dev/urandom of=./write_IO_interfere.tmp bs=1000M count=1 conv=fdatasync
dd if=/dev/zero of=./write_IO_interfere.tmp bs=1000M count=1 conv=fdatasync
#sleep 1
done
