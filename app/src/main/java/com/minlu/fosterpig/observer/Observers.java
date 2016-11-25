package com.minlu.fosterpig.observer;

/**
 * 所有被通知对象的父类接口
 * */
public interface Observers {
	public void update(int mPosition, int distinguishBeNotified, int cancelOrderBid);
}
