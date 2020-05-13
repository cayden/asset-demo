package com.cayden.asset.controller;

import com.cayden.asset.service.AssetService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

/**
 * Created by cuiran on 19/11/9.
 */
@RestController
@RequestMapping("/asset")
public class AssetController {


    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

    @Autowired
    private AssetService assetService;


    @PostMapping("/deploy")
    public String deploy(){
        logger.info("list: {}", "deploy");
        try {
            return assetService.deploy();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "参数错误!";
    }


    @PostMapping("/register")
    public TransactionReceipt register(String account,BigInteger amount){
        logger.info("candidate: {}", account);
        try {
            return assetService.register(account, amount);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/transfer")
    public TransactionReceipt transfer(String from_account, String to_account,BigInteger amount){
        logger.info("from_account: {},to_account:{},amount:{}", from_account,to_account,amount);
        try {
            return assetService.transfer(from_account, to_account, amount);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/select")
    public BigInteger select(String account){
        logger.info("account: {}", account);
        try {
            return assetService.select(account);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
