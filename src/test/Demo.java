package test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Demo {
	public static void main(String[] args) {
		Demo demo=new Demo();
		demo.go();

	}
	public void go(){
		String path="C:/Users/ionassef/Desktop/";
		File f = new File(path);
		if (f.isDirectory()){
		   FilenameFilter filter =  new FilenameFilter() {
		            @Override
		            public boolean accept(File dir, String name) {
		                if(name.matches("orders\\d{2}.xml")){
		                    return true;
		                }
		                return false;
		            }
		        };
		   if (f.list(filter).length > 0){
			   for(String s:f.list(filter)){
				   System.out.println(s);
				   String orderid=s.substring(s.indexOf(".") -2, s.indexOf("."));
				   File inputFile = new File(path+s);
				   Demo.computeXML(inputFile,orderid);
			   }
		   }
		}
	}
	public static void computeXML(File inputFile,String nr){
		try {
			//File inputFile = new File("C:/Users/ionassef/Desktop/orders23.xml");
			List<Product> empList = new ArrayList<>();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("order");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String order_id = eElement.getAttribute("ID");
					String order_created = eElement.getAttribute("created");
					NodeList productsList = eElement
							.getElementsByTagName("product");

					for (int count = 0; count < productsList.getLength(); count++) {
						Node node1 = productsList.item(count);

						if (node1.getNodeType() == node1.ELEMENT_NODE) {
							Product aux = new Product();
							Element prod = (Element) node1;
							aux.setDescription(prod
									.getElementsByTagName("description")
									.item(0).getTextContent());
							aux.setGtin(prod.getElementsByTagName("gtin")
									.item(0).getTextContent());
							aux.setOrderid(order_id);
							aux.setPrice(Double.parseDouble(prod
									.getElementsByTagName("price").item(0)
									.getTextContent()));
							aux.setCurrency(((Element) prod
									.getElementsByTagName("price").item(0))
									.getAttribute("currency"));
							aux.setSupplier(prod
									.getElementsByTagName("supplier").item(0)
									.getTextContent());
							aux.setCreated(order_created);
							empList.add(aux);
						}
					}
				}
			}
//			Iterator<Product> prodIterator = empList.iterator();
//			while (prodIterator.hasNext()) {
//				Product aux = prodIterator.next();
//			}
			// sort
			CompareProduct comp = new CompareProduct();
			Collections.sort(empList, comp);
			// System.out.println("==========");
//			Iterator<Product> prodIterator1 = empList.iterator();
//			while (prodIterator1.hasNext()) {
//				Product aux = prodIterator1.next();
//			}
			// get unique
			Set<String> gasNames = new HashSet<String>();
			for (Product record : empList) {
				gasNames.add(record.getSupplier());
			}
			List<String> sortedGasses = new ArrayList<String>(gasNames);
			Iterator<String> uniq = sortedGasses.iterator();

			while (uniq.hasNext()) {
				String value = uniq.next();
				DocumentBuilderFactory findbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder findBuilder = findbFactory.newDocumentBuilder();
				Document fin_doc = findBuilder.newDocument();
				Element fin_RootElement = fin_doc.createElement("products");
				fin_doc.appendChild(fin_RootElement);
				Iterator<Product> finprodIterator = empList.iterator();
				while (finprodIterator.hasNext()) {
					Product aux = finprodIterator.next();
					if (aux.getSupplier().equals(value)) {
						Element prod = fin_doc.createElement("product");
						fin_RootElement.appendChild(prod);

						Element fin_description = fin_doc
								.createElement("description");
						fin_description.appendChild(fin_doc.createTextNode(aux
								.getDescription()));
						prod.appendChild(fin_description);

						Element fin_gtin = fin_doc.createElement("gtin");
						fin_gtin.appendChild(fin_doc.createTextNode(aux.getGtin()));
						prod.appendChild(fin_gtin);

						Element fin_price = fin_doc.createElement("price");
						Attr priceType = fin_doc.createAttribute("currency");
						priceType.setValue(aux.getCurrency());
						fin_price.setAttributeNode(priceType);
						fin_price.appendChild(fin_doc.createTextNode(String
								.valueOf(aux.getPrice())));
						prod.appendChild(fin_price);

						Element fin_orderid = fin_doc.createElement("orderid");
						fin_orderid.appendChild(fin_doc.createTextNode(aux
								.getOrderid()));
						prod.appendChild(fin_orderid);

					}
				}
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(fin_doc);
				String folder = "C:/Users/ionassef/Desktop/Results/";
				Path path = Paths.get(folder);
				if(!Files.exists(path)) {
				    try {
				      Files.createDirectories(path);
				    } catch (IOException e) {
				      e.printStackTrace();
				    }
				}
				//String nr = "23";
				String fin_path = folder + value + nr + ".xml";
				StreamResult result = new StreamResult(new File(fin_path));
				transformer.transform(source, result);
				StreamResult consoleResult = new StreamResult(System.out);
				transformer.transform(source, consoleResult);
				System.out.println("File saved!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
