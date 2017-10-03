package hu.kovacshazianna.service;

import static java.util.Objects.requireNonNull;

/**
 * Handles storage related operations.
 *
 * @author Anna_Kovacshazi
 */
public interface StorageManager {

    /**
     * Allocate blocks.
     * @param numBlocksRequired number of block to be allocated
     * @return data block
     */
    DataBlock allocate(int numBlocksRequired);

    /**
     * Release a data block.
     * @param dataBlock data block to be released
     * @return boolean value for the status of the operation
     */
    boolean release(DataBlock dataBlock);

    /**
     * Represents a data block.
     *
     * @author Anna_Kovacshazi
     */
    class DataBlock {

        private static final Object GUARD = new Object();
        private DelegatableDataBlockIO delegatable;

        public DataBlock(DelegatableDataBlockIO delegatable) {
            this.delegatable = requireNonNull(delegatable);
        }

        public boolean write(byte[] data) {
            synchronized (GUARD) {
                return delegatable.write(this, data);
            }
        }

        public byte[] read() {
            synchronized (GUARD) {
                return delegatable.read(this);
            }
        }

        @Override
        public boolean equals(Object o) {
            //intentionally called super, equality based on reference
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            //intentionally called super
            return super.hashCode();
        }
    }
}
