package com.massvig.ecommerce.entities;

import java.util.ArrayList;

public class MessageList {

	private ArrayList<Message> messageList;
	
	public ArrayList<Message> getMessageList() {
		return messageList;
	}

	public void setMessageList(ArrayList<Message> messageList) {
		this.messageList = messageList;
	}

	public MessageList(){
		messageList = new ArrayList<Message>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearmessageList(){
		if(messageList !=null && messageList.size() > 0){
			messageList.clear();
		}
	}
	/**
	 * 删除一个Message
	 * @param position
	 */
	public void deleteMessage(int position){
		messageList.remove(position);
	}
	
	/**
	 * 增加一个Message
	 * @param Message
	 */
	public void addMessage(Message Message){
		messageList.add(Message);
	}
	
	/**
	 * 增加一个messageList
	 * @param list
	 */
	public void addmessageList(MessageList list){
		messageList.addAll(list.getmessageList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Message> getmessageList() {
		// TODO Auto-generated method stub
		return messageList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(messageList != null){
			return messageList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定Message
	 * @param position
	 * @return
	 */
	public Message getMessage(int position){
		return messageList.get(position);
	}
	
}
