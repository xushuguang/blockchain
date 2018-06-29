package com.james.noobchain;

import com.james.blockchain.StringUtil;

import java.security.PublicKey;

public class TransactionOutput {

    public String id;
    public PublicKey reciepient; //这些硬币的新主人。
    public float value; //拥有的硬币数量
    public String parentTransactionId; //创建的交易的ID

    //构造函数
    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
    }

    //检查硬币是否属于你
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }
}
