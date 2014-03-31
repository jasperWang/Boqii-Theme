//package com.massvig.ecommerce.entities;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//
//import org.xml.sax.Attributes;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.Locator;
//import org.xml.sax.SAXException;
//
//import com.massvig.ecommerce.service.MassVigService;
//
//public class CopyOfCityList implements ContentHandler, Serializable{
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -2981614739426599392L;
//
//	ArrayList<City> mCities;
//	
//	private String content;
//	
//	private City city;
//	
//	private int fetchCitiesCount;
//
//	public CopyOfCityList () {
//		mCities = new ArrayList<City>();
//	}
//	
//	public int fetchCities()
//	{
//			MassVigService service = MassVigService.getInstance();
//			
//			fetchCitiesCount = 0;
//			service.getCities(this);
//			return fetchCitiesCount;
//	}
//	
//	public int getCount() {
//		return mCities.size();
//	}
//	
//	public Object getItem(int position) {
//		return mCities.get(position);
//	}
//
//	public long getItemId(int position) {
//		return mCities.get(position).getCityId();
//	}
//	
//	public City getCity(int position) {
//		if((mCities == null)||(mCities.size() == 0)){
//			return null;
//		}
//		if(position > mCities.size() - 1){
//			return null;
//		}
//		return mCities.get(position);
//	}
//	
//	public City getCityByName(String name) {
//		City result = null;
//		
//		if (name != null) {
//			for (City city : mCities) {
//				if (city.getCityName().regionMatches(0, name, 0, 2)) {
//					result = city;
//					break;
//				}
//			}
//		}
//		
//		//FIXME: zhxu fix this
//		if (result == null) {
//			result = new City();
//			result.setCityId(339);
//			result.setCityName("上海");
//			result.setCityNamePY("s");
//			result.setIsHot(true);
//		}
//		
//		return result;
//	}
//
//	public void characters(char[] ch, int start, int length) throws SAXException {
//		content = content.concat(new String(ch, start, length));
//	}
//
//	public void startDocument() throws SAXException {
//		content = new String();
//		
//	}
//	
//	public void endDocument() throws SAXException {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	public void startElement(String uri, String localName, String qName,
//			Attributes atts) throws SAXException {
//		
//		if (localName.equals("City")) {
//			city = new City();
//		}
//		
//		content = new String();
//	}
//
//	public void endElement(String uri, String localName, String qName)
//			throws SAXException {
//		
//		if (localName.equals("CityID")) {
//			city.setCityId(Integer.parseInt(content));
//		} else if (localName.equals("CityName")) {
//			city.setCityName(content);
//		} else if (localName.equals("CityNameFirstChar")) {
//			city.setCityNamePY(content);
//			
//		} else if (localName.equals("IsHot")) {
//			city.setIsHot(!content.equals("0"));
//		} else if (localName.equals("City")) {
//			this.mCities.add(city);
//			fetchCitiesCount++;
//		} else {
//
//		}
//		
//	}
//
//	public void endPrefixMapping(String arg0) throws SAXException {
//		
//	}
//
//	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
//			throws SAXException {
//		
//	}
//
//	public void processingInstruction(String arg0, String arg1)
//			throws SAXException {
//		
//	}
//
//	public void setDocumentLocator(Locator arg0) {
//		
//	}
//
//	public void skippedEntity(String arg0) throws SAXException {
//		
//	}
//
//	public void startPrefixMapping(String arg0, String arg1)
//			throws SAXException {
//		
//	}
//	
//}
