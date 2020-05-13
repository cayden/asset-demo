package com.cayden.asset.service;

import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;

import java.math.BigInteger;

/**
 * Created by cuiran on 19/11/9.
 */
public interface AssetService {

    public String deploy() throws Exception;


    public TransactionReceipt register(String account,BigInteger amount) throws Exception;

    public TransactionReceipt transfer(String from_account, String to_account, BigInteger amount) throws Exception;

    public BigInteger select(String account) throws Exception;
}
