package com.cayden.asset.service.impl;

import com.cayden.asset.config.ContractConfig;
import com.cayden.asset.constant.WeIdConstant;
import com.cayden.asset.contract.Asset;
import com.cayden.asset.service.AssetService;
import com.cayden.asset.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * Created by cuiran on 19/11/9.
 */
@Service
@Component
public class AssetServiceImpl extends BaseService implements AssetService {

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);

    private static Asset asset;

    private static String assetAddress;

    public AssetServiceImpl() {
        init();
    }


    private static void init() {
        ContractConfig config = context.getBean(ContractConfig.class);
        assetAddress = config.getAssetAddress();
        asset = (Asset) getContractService(assetAddress,
                Asset.class);
    }


    @Override
    public String deploy() throws Exception {

        try {

            asset = Asset.deploy(
                    web3j,
                    credentials,
                    new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT)
                    )
                    .send();

        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }

        return asset.getContractAddress();
    }

    @Override
    public TransactionReceipt register(String account, BigInteger amount) throws Exception {

        TransactionReceipt voteReceipt = null;
        if (asset.isValid()) {
            voteReceipt = asset.register(account,amount).sendAsync().get();
            logger.info("register: {}", voteReceipt);
        }
        return voteReceipt;
    }

    @Override
    public TransactionReceipt transfer(String from_account, String to_account, BigInteger amount) throws Exception {
        TransactionReceipt voteReceipt = null;
        if (asset.isValid()) {
            voteReceipt = asset.transfer(from_account,to_account, amount).sendAsync().get();
            logger.info("register: {}", voteReceipt);
        }
        return voteReceipt;
    }

    @Override
    public BigInteger select(String account) throws Exception {
        BigInteger amount=null;
        if (asset.isValid()) {
            Tuple2<BigInteger, BigInteger> tuple2=  asset.select(account).sendAsync().get();
            amount=tuple2.getValue2();
            logger.info("register: {}", tuple2);
        }
        return amount;
    }
}
