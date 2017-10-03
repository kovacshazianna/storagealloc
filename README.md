DESCRIPTION

The problem

Given a fixed amount of memory (100,000 blocks of 1024 bytes) we require a
system that allows clients to request and have allocated quantized blocks of
storage. This is analogous to the allocation of memory from a heap.

Once a client has been assigned a block (or set of blocks) they must then be
able to write and read arbitrary binary data to and from it. In addition once an
area of memory has been assigned it must not be reassigned until it has been
released.

CONTACT
Author: Kovácsházi Anna
e-mail: kovacshazianna@gmail.com