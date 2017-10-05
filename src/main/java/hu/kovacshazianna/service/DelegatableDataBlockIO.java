package hu.kovacshazianna.service;

import hu.kovacshazianna.service.StorageManager.DataBlock;

/**
 * Class for data block operations.
 *
 * @author Anna_Kovacshazi
 */
abstract class DelegatableDataBlockIO {

    /**
     * Writes data into a {@link DataBlock}.
     * @param dataBlock the data block
     * @param data the data to be written
     * @return boolean value for the status of the write
     */
    abstract boolean write(DataBlock dataBlock, byte[] data);

    /**
     * Reads data from {@link DataBlock}.
     * @param dataBlock the data block
     * @return byte array representation of the data
     */
    abstract byte[] read(DataBlock dataBlock);
}
