/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.harvest.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.*;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.CDATA;
import org.dom4j.CharacterData;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.QName;
import org.dom4j.Text;

/** An abstract base class for some DOM4J test cases. This abstract case has
  * asserts that can check to see if nodes are equal and why they are different 
  * if applicable
  *
  * @author James Strachan and altered by Dave Finke to enable the nodes and attributes
  * to be in different order
  */
public class AbstractTestCase extends TestCase {

    protected static final boolean COMPARE_TEXT = false;
    protected Document document;
    
    
    public AbstractTestCase(String name) {
        super(name);
    }

    public void log(String text) {
        System.out.println(text);
    }
    
    
    public void assertDocumentsEqual(Document doc1, Document doc2) throws Exception {
        try {
            assertTrue( "Doc1 not null", doc1 != null );
            assertTrue( "Doc2 not null", doc2 != null );

            doc1.normalize();
            doc2.normalize();

            assertNodesEqual(doc1.getRootElement(), doc2.getRootElement());

            //NodeComparator comparator = new NodeComparator();
            //assertTrue( "Documents are equal", comparator.compare( doc1, doc2 ) == 0 );

            if ( COMPARE_TEXT ) {
                String text1 = doc1.asXML();
                String text2 = doc2.asXML();

                assertEquals( "Text of documents is equal", text1, text2 );
            }
        }
        catch (Exception e) {
            log( "Failed during comparison of: " + doc1 + " and: " + doc2 );
            throw e;
        }
    }

    
    public void assertNodesEqual( Document n1, Document n2 ) {
        assertEquals( "Document names", n1.getName(), n2.getName() );        
        assertNodesEqual( n1.getDocType(), n2.getDocType() );        
        assertNodesEqualContent( n1, n2 );
    }
    
    public void assertNodesEqual( Element n1, Element n2 ) {
        assertNodesEqual( n1.getQName(), n2.getQName() );
        assertEquals(n1.getNamespace(), n2.getNamespace());
        
        List<Namespace> namesSpaces1 = n1.additionalNamespaces();
        List<Namespace> namesSpaces2 = n2.additionalNamespaces();
        
        int namesSpaces1Count = namesSpaces1.size();
        int namesSpaces2Count = namesSpaces2.size();
        assertEquals( 
                "Elements have same number of namespaces (" + namesSpaces1Count + ", " + namesSpaces2Count 
                    + " for: " + n1 + " and " + n2,
                    namesSpaces1Count, namesSpaces2Count 
            );
        
        Collections.sort(namesSpaces1, new NamespaceComparator());
        Collections.sort(namesSpaces2, new NamespaceComparator());
        
        for ( int i = 0; i < namesSpaces1Count; i++ ) {
        	Namespace namespace1 = namesSpaces1.get(i);
        	Namespace namespace2 = namesSpaces2.get(i);
            assertNodesEqual( namespace1, namespace2 );
        }
        
        
        int c1 = n1.attributeCount();
        int c2 = n2.attributeCount();
        
        assertEquals( 
            "Elements have same number of attributes (" + c1 + ", " + c2 
                + " for: " + n1 + " and " + n2,
            c1, c2 
        );
       
        List<Attribute> attributes1 = n1.attributes();
        List<Attribute> attributes2 = n2.attributes();
        
        Collections.sort(attributes1, new AttributeComparator());
        Collections.sort(attributes2, new AttributeComparator());
        for ( int i = 0; i < c1; i++ ) {
            Attribute a1 = attributes1.get(i);
            Attribute a2 = attributes2.get(i);
            assertNodesEqual( a1, a2 );
        }
        
        List<Element> elements1 = n1.elements();
        List<Element> elements2 = n2.elements();
        
        int eCount1 = elements1.size();
        int eCount2 = elements2.size();
        
        assertEquals( 
                "Elements have same number of elements (" + eCount1 + ", " + eCount2 
                    + " for: " + n1 + " and " + n2,
                    eCount1, eCount2 
            );
        
   
        Collections.sort(elements1, new ElementComparator());
        Collections.sort(elements2, new ElementComparator());
        
        
        for ( int i = 0; i < eCount1; i++ ) {
        	Element e1 = elements1.get(i);
        	Element e2 = elements2.get(i);
            assertNodesEqual( e1, e2 );
        }
        
        if(eCount1==0)
        	assertEquals(String.format("Elements have the same text. -%s-, -%s-",
        			n1.getName(), n2.getName()), n1.getText(), n2.getText());
    }
    
    
    private class ElementComparator implements Comparator<Element> {

        public int compare(Element e1, Element e2) {

        	int namespacePrefixCompare = e1.getNamespacePrefix().compareTo(e2.getNamespacePrefix());
        	if(namespacePrefixCompare!=0)
        		return namespacePrefixCompare;
        	int nameCompare = e1.getName().compareTo(e2.getName());
        	if (nameCompare!=0)
        		return nameCompare;
        	return e1.getText().compareTo(e2.getText());
        }
    }
    private class NamespaceComparator implements Comparator<Namespace> {

        public int compare(Namespace n1, Namespace n2) {
            
            int prefixCompare = n1.getPrefix().compareTo(n2.getPrefix());
        	if(prefixCompare!=0)
        		return prefixCompare;
        	return n1.getURI().compareTo(n2.getURI());

        }
    }
    private class AttributeComparator implements Comparator<Attribute> {

        public int compare(Attribute a1, Attribute a2) {
            
            return a1.getName().compareTo(a2.getName());


        }
    }
    
    public void assertNodesEqual( Attribute n1, Attribute n2 ) {
        assertNodesEqual( n1.getQName(), n2.getQName() );
        
        assertEquals( 
            "Attribute values for: " + n1 + " and " + n2,
            n1.getValue(), n2.getValue() 
        );
    }
    
    public void assertNodesEqual( QName n1, QName n2 ) {
        assertEquals( 
            "URIs equal for: " + n1.getQualifiedName() + " and " + n2.getQualifiedName(),
            n1.getNamespaceURI(), n2.getNamespaceURI() 
        );
        assertEquals( 
            "qualified names equal",
            n1.getQualifiedName(), n2.getQualifiedName() 
        );
    }
    
    public void assertNodesEqual( CharacterData t1, CharacterData t2 ) {
        assertEquals( 
            "Text equal for: " + t1 + " and " + t2,
            t1.getText().trim(), t2.getText().trim()
        );
    }
    
    public void assertNodesEqual( DocumentType o1, DocumentType o2 ) {
        if ( o1 != o2 ) {
            if ( o1 == null ) {
                assertTrue( "Missing DocType: " + o2, false );
            }
            else if ( o2 == null ) {
                assertTrue( "Missing DocType: " + o1, false );
            }
            else {
                assertEquals( "DocType name equal", o1.getName(), o2.getName() );
                assertEquals( "DocType publicID equal", o1.getPublicID(), o2.getPublicID() );
                assertEquals( "DocType systemID equal", o1.getSystemID(), o2.getSystemID() );
            }
        }
    }
    
    public void assertNodesEqual( Entity o1, Entity o2 ) {
        assertEquals( "Entity names equal", o1.getName(), o2.getName() );
        assertEquals( "Entity values equal", o1.getText(), o2.getText() );
    }
    
    public void assertNodesEqual( ProcessingInstruction n1, ProcessingInstruction n2 ) {
        assertEquals( "PI targets equal", n1.getTarget(), n2.getTarget() );
        assertEquals( "PI text equal", n1.getText(), n2.getText() );
    }
    
    public void assertNodesEqual( Namespace n1, Namespace  n2 ) {
        assertEquals( "Namespace prefixes equal", n1.getPrefix(), n2.getPrefix() );
        assertEquals( "Namespace URIs equal", n1.getURI(), n2.getURI() );
    }
    
    public void assertNodesEqualContent( Branch b1, Branch b2 ) {
    	 List<Node> nodeList1 = b1.selectNodes("child::node()");
         List<Node> nodeList2 = b2.selectNodes("child::node()");
         
         for (Node node: nodeList1)
         {
        	 if (node.getNodeType()==Node.TEXT_NODE && node.getText().replace("\n", "").replace("\r", "").trim()=="")
        	 {
        		 nodeList1.remove(node);
        	 }

         }
         for (Node node: nodeList2)
         {
        	 if (node.getNodeType()==Node.TEXT_NODE && node.getText().replace("\n", "").replace("\r", "").trim()=="")
        	 {
        		 nodeList2.remove(node);
        	 };
         }
    	
    	
         /*System.out.println("Next I say");
         for(Node node: nodeList1)
         {
        	 System.out.println("Name " +node.getName()+ " text " + node.getText());
         }
         System.out.println("Node 2");
         for(Node node: nodeList2)
         {
        	 System.out.println("Name " +node.getName()+ " text " + node.getText());
         }*/
         
    	
        int c1 = b1.nodeCount();
        int c2 = b2.nodeCount();
        
        if ( c1 != c2 ) {
            log( "Content of: " + b1 );
            log( "is: " + b1.content() );
            log( "Content of: " + b2 );
            log( "is: " + b2.content() );
        }
        
        assertEquals( 
            "Branches have same number of children (" + c1 + ", " + c2 
                + " for: " + b1 + " and " + b2,
            c1, c2 
        );
        
        for ( int i = 0; i < c1; i++ ) {
            Node n1 = nodeList1.get(i);
            Node n2 = nodeList2.get(i);
            
            System.out.println("Compare " + n1.getName() + "To " + n2.getName());
        }
        
        for ( int i = 0; i < c1; i++ ) {
        	Node n1 = nodeList1.get(i);
            Node n2 = nodeList2.get(i);
            assertNodesEqual( n1, n2 );
        }
    }
    

	public void assertNodesEqual( Node n1, Node n2 ) {
        int nodeType1 = n1.getNodeType();
        int nodeType2 = n2.getNodeType();
        assertTrue( "Nodes are of same type: ", nodeType1 == nodeType2 );
        
        switch (nodeType1) {
            case Node.ELEMENT_NODE:
                assertNodesEqual((Element) n1, (Element) n2);
                break;
            case Node.DOCUMENT_NODE:
                assertNodesEqual((Document) n1, (Document) n2);
                break;
            case Node.ATTRIBUTE_NODE:
                assertNodesEqual((Attribute) n1, (Attribute) n2);
                break;
            case Node.TEXT_NODE:
                assertNodesEqual((Text) n1, (Text) n2);
                break;
            case Node.CDATA_SECTION_NODE:
                assertNodesEqual((CDATA) n1, (CDATA) n2);
                break;
            case Node.ENTITY_REFERENCE_NODE:
                assertNodesEqual((Entity) n1, (Entity) n2);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                assertNodesEqual((ProcessingInstruction) n1, (ProcessingInstruction) n2);
                break;
            case Node.COMMENT_NODE:
                assertNodesEqual((Comment) n1, (Comment) n2);
                break;
            case Node.DOCUMENT_TYPE_NODE:
                assertNodesEqual((DocumentType) n1, (DocumentType) n2);
                break;
            case Node.NAMESPACE_NODE:
                assertNodesEqual((Namespace) n1, (Namespace) n2);
                break;
            default:
                assertTrue( "Invalid node types. node1: " + n1 + " and node2: " + n2, false );
        }
    }
    

    // Implementation methods
    //-------------------------------------------------------------------------                    
    protected void setUp() throws Exception {
        document = createDocument();
        
        Element root = document.addElement( "root" );
        
        Element author1 = root.addElement( "author" )
            .addAttribute( "name", "James" )
            .addAttribute( "location", "UK" )
            .addText("James Strachan");

        Element url1 = author1.addElement( "url" )
            .addText( "http://sourceforge.net/users/jstrachan/" );
        
        Element author2 = root.addElement( "author" )
            .addAttribute( "name", "Bob" )
            .addAttribute( "location", "Canada" )
            .addText("Bob McWhirter");
        
        Element url2 = author2.addElement( "url" )
            .addText( "http://sourceforge.net/users/werken/" );
    }

    protected Document createDocument() throws Exception {
        return DocumentHelper.createDocument();
    }
        
        
    /** @return the root element of the document */
    protected Element getRootElement() {
        Element root = document.getRootElement();
        assertTrue( "Document has root element", root != null );
        return root;
    }

        
}




/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "DOM4J" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of MetaStuff, Ltd.  For written permission,
 *    please contact dom4j-info@metastuff.com.
 *
 * 4. Products derived from this Software may not be called "DOM4J"
 *    nor may "DOM4J" appear in their names without prior written
 *    permission of MetaStuff, Ltd. DOM4J is a registered
 *    trademark of MetaStuff, Ltd.
 *
 * 5. Due credit should be given to the DOM4J Project
 *    (http://dom4j.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * METASTUFF, LTD. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2001 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * $Id: AbstractTestCase.java,v 1.14 2001/08/17 08:16:03 jstrachan Exp $
 */
