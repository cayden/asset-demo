package com.cayden.asset.service;

import com.cayden.asset.constant.WeIdConstant;
import com.cayden.asset.exception.InitWeb3jException;
import com.cayden.asset.exception.LoadContractException;
import com.cayden.asset.exception.PrivateKeyIllegalException;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.tx.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

/**
 * Created by caydencui on 2019/6/4.
 */

public abstract class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected static final ApplicationContext context;

    public static Credentials credentials;

    public static Web3j web3j;

    static {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    private static boolean initWeb3j() {
        Service service = context.getBean(Service.class);
        try {
            service.run();
        } catch (Exception e) {
            logger.error("[BaseService] Service init failed. ", e);
            throw new InitWeb3jException(e);
        }
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        web3j = Web3j.build(channelEthereumService);
        if (null == web3j) {
            logger.error("[BaseService] web3j init failed. ");
            return false;
        }
        return true;
    }

    /**
     * Inits the credentials.
     *
     * @return true, if successful
     */
    private static boolean initCredentials() {

        ECKeyPair keyPair = null;
        try {
            keyPair = Keys.createEcKeyPair();
        } catch (Exception e) {
            logger.error("Create weId failed.", e);
            return false;
        }
        credentials = Credentials.create(keyPair);

        if (credentials == null) {
            logger.error("[BaseService] credentials init failed. ");
            return false;
        }
        return true;
    }

    /**
     * Gets the web3j.
     *
     * @return the web3j
     */
    protected static Web3j getWeb3j() {
        if (null == web3j) {
            if (!initWeb3j()) {
                throw new InitWeb3jException();
            }
        }
        return web3j;
    }

    private static Object loadContract(
            String contractAddress,
            Credentials credentials,
            Class<?> cls) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        Object contract;
        Method method = cls.getMethod(
                "load",
                String.class,
                Web3j.class,
                Credentials.class,
                BigInteger.class,
                BigInteger.class
        );

        contract = method.invoke(
                null,
                contractAddress,
                getWeb3j(),
                credentials,
                WeIdConstant.GAS_PRICE,
                WeIdConstant.GAS_LIMIT
        );
        return contract;
    }

    /**
     * Reload contract.
     *
     * @param contractAddress the contract address
     * @param privateKey the privateKey of the sender
     * @param cls the class
     * @return the contract
     */
    protected static Contract reloadContract(
            String contractAddress,
            String privateKey,
            Class<?> cls) {
        Credentials credentials;
        try {
            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
            credentials = Credentials.create(keyPair);
        } catch (Exception e) {
            throw new PrivateKeyIllegalException(e);
        }

        Object contract = null;
        try {
            // load contract
            contract = loadContract(contractAddress, credentials, cls);
            logger.info(cls.getSimpleName() + " init succ");
        } catch (Exception e) {
            logger.error("load contract :{} failed. Error message is :{}",
                    cls.getSimpleName(), e);
            throw new LoadContractException();
        }

        if (contract == null) {
            throw new LoadContractException();
        }
        return (Contract) contract;
    }

    /**
     * Gets the contract service.
     *
     * @param contractAddress the contract address
     * @param cls the class
     * @return the contract service
     */
    protected static Contract getContractService(String contractAddress, Class<?> cls) {

        Object contract = null;
        try {
            // load contract
            if (null == credentials) {
                initCredentials();
            }
            contract = loadContract(contractAddress, credentials, cls);
            logger.info(cls.getSimpleName() + " init succ");

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("load contract :{} failed. Error message is :{}",
                    cls.getSimpleName(), e);
            throw new LoadContractException();
        } catch (Exception e) {
            logger.error("load contract Exception:{} failed. Error message is :{}",
                    cls.getSimpleName(), e);
            throw new LoadContractException();
        }

        if (contract == null) {
            throw new LoadContractException();
        }
        return (Contract) contract;
    }

    public  byte[] stringtobyte32(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return byteValueLen32;

    }
}
