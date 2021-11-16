package com.wavesplatform.transactions;

import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;

import java.util.Collections;

public class EthereumTransaction extends TransactionOrOrder {
    public EthereumTransaction(byte chainId, PublicKey sender, long fee, long timestamp) {
        super(0, chainId, sender, Amount.of(fee), timestamp, Collections.emptyList());
    }
}
