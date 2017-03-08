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
package edu.ucar.dls.schemedit.test;
import java.util.*;




/**
 *  Description of the Class
 *
 * @author    ostwald
 */
public class SchemaRegistry {

	Map schemaMap = null;

	
	public SchemaRegistry () {
		schemaMap = getSchemaMap ();
	}

	public Schema getSchema (String schemaName) {
		return (Schema) schemaMap.get (schemaName);
	}
	
	public static String getSchemaPath (String xmlFormat) {
		SchemaRegistry sr = new SchemaRegistry ();
		Schema schema = sr.getSchema (xmlFormat);
		if (schema != null) {
			return schema.path;
		}
		else {
			return null;
		}
	}
	
	void registerSchema (String name, String path, String rootElementName) {
		schemaMap.put (name, new Schema (name, path, rootElementName));
	}
	
	Map getSchemaMap () {
		if (schemaMap == null) {
			schemaMap = new HashMap();
					
			registerSchema ("dlese_anno",
				"http://www.dlese.org/Metadata/annotation/1.0.00/annotation.xsd",
				"annotationRecord");

			registerSchema ("oai_dc",
				"http://www.dlese.org/Metadata/oai_dc/2.0/oai_dc-DCvocab-nolang.xsd",
				"oai_dc:dc");
				
			registerSchema ("dlese_collect",
				// "/devel/ostwald/metadata-frameworks/collection-v1.0.00/collection.xsd",
				"http://www.dlese.org/Metadata/collection/1.0.00/collection.xsd",
				"");

			registerSchema ("news_opps",
				"http://www.dlese.org/Metadata/news-opps/1.0.00/news-opps.xsd",
				"news-oppsRecord");
				
			registerSchema ("adn",
				"http://www.dlese.org/Metadata/adn-item/0.6.50/record.xsd",
				"itemRecord");
				
			registerSchema ("mets",
				"http://www.loc.gov/standards/mets/mets.xsd",
				"this:mets");
				
			registerSchema ("attributeGroup",
				"http://www.dpc.ucar.edu/people/ostwald/Metadata/AttributeGroupTester/schema.xsd",
				"record");	
				
			registerSchema ("restriction-tester",
				"http://www.dpc.ucar.edu/people/ostwald/Metadata/AttributeGroupTester/restriction-tester.xsd",
				"records");		
				
			registerSchema ("extension-tester",
				"http://www.dpc.ucar.edu/people/ostwald/Metadata/AttributeGroupTester/extension-tester.xsd",
				"records");
				
			registerSchema ("play",
				"http://www.dpc.ucar.edu/people/ostwald/Metadata/NameSpacesPlay/cd.xsd",
				"cd:cd");
				
			registerSchema ("ncs_collect",
				"http://ns.nsdl.org/ncs/ncs_collect/1.02/schemas/ncs-collect.xsd",
				// "C:/Documents and Settings/ostwald/devel/projects/ncs/ncs_collect/1.02/schemas/ncs-collect.xsd",
				"record");		

			registerSchema ("ncs_item",
				"http://ns.nsdl.org/ncs/ncs_item/1.02/schemas/ncs-item.xsd",
				"record");	
				
			registerSchema ("collection_config",
				 "http://www.dpc.ucar.edu/people/ostwald/Metadata/collection-config/dcsCollectionConfig.xsd", 
				/*"http://localhost/metadata-frameworks/collection_config/dcsCollectionConfig.xsd",*/
				"collectionConfigRecord");
				
			registerSchema ("framework_config",
				/* "http://www.dpc.ucar.edu/people/ostwald/Metadata/framework-config/dcsFrameworkConfig.xsd", */
				"http://localhost/metadata-frameworks/framework-config/dcsFrameworkConfig.xsd",
				"frameworkConfigRecord");
				
			registerSchema ("dcs_data",
				"http://www.dpc.ucar.edu/people/ostwald/Metadata/dcs-data/dcs-data.xsd",
				"dcsDataRecord");
				
			registerSchema ("mast",
				"http://www.dlsciences.org/frameworks/mast/1.0/schemas/mast-dc-gem.xsd",
				"record");	
				
			registerSchema ("concepts",
				"http://www.dlsciences.org/frameworks/concepts/1.0/schemas/concepts.xsd",
				"concept");	
				
			registerSchema ("my_concepts",
				"http://www.dls.ucar.edu/people/ostwald/Metadata/conceptsjlo/1.0/schemas/concepts.xsd",
				"concept");
				
			registerSchema ("sif_activity",
				"http://www.dls.ucar.edu/people/ostwald/Metadata/sif/SIF_Message.xsd",
				"sif:Activity");

			registerSchema ("sif_learning_resource",
				"http://www.dls.ucar.edu/people/ostwald/Metadata/sif/SIF_Message.xsd",
				"sif:LearningResource");
				
			registerSchema ("fields_file",
				"http://www.dls.ucar.edu/people/ostwald/Metadata/fields/non-vocab-fields.xsd",
				"metadataFieldInfo");	
				
			registerSchema ("ead",
				"http://www.loc.gov/ead/ead.xsd",
				"ead");	
				
			registerSchema ("library_dc",
				"file:////Users/ostwald/devel/projects/frameworks-project/frameworks/library_dc/1.1/schemas/library_dc.xsd",
				// "http://www.dlsciences.org/frameworks/library_dc/1.0/schemas/library_dc.xsd",
				"library_dc:record");
				
			registerSchema ("smile_item",
				"http://howtosmile.org/Metadata/smile-item/0.1.4/smile-item.xsd",
				"smileItem");	
				
			registerSchema ("choice_tester",
				"http://www.dpc.ucar.edu/people/ostwald/Metadata/choice_tester/choice-tester.xsd",
				"choice_testerRecord");	

			registerSchema ("name_tester",
				"http://www.dpc.ucar.edu/people/ostwald/Metadata/choice_tester/name-tester.xsd",
				"choice_testerRecord");
				
			registerSchema ("msp2",
				/* "http://ns.nsdl.org/ncs/msp2/1.00/schemas/record.xsd", */
				"http://www.dls.ucar.edu/people/ostwald/ncs/msp2/1.00/schemas/record.xsd", // local version!
				"record");
						
			registerSchema ("ncs_user",
				"http://localhost/metadata-frameworks/ncs_user/ncsUserFramework.xsd",
				"record");
				
			registerSchema ("res_qual",
				"http://ns.nsdl.org/ncs/res_qual/1.00/schemas/record.xsd",
				"record");
				
			registerSchema ("osm",
				"http://nldr.library.ucar.edu/metadata/osm/1.0/schemas/osm.xsd",
				"record");
				
			registerSchema ("osm_next",
				"http://nldr.library.ucar.edu/metadata/osm/1.1/schemas/osm.xsd",
				"record");
				
			registerSchema ("eng_path",
				"http://ns.nsdl.org/ncs/eng_path/1.00/schemas/record.xsd",
				"record");
				
			registerSchema ("nsdl_anno",
				"http://ns.nsdl.org/ncs/nsdl_anno/1.00/schemas/nsdl_anno.xsd",
				"nsdl_anno");

			registerSchema ("comm_anno",
				"http://ns.nsdl.org/ncs/comm_anno/1.00/schemas/comm_anno.xsd",
				"comm_anno");
			
			registerSchema ("math_path",
				"http://ns.nsdl.org/ncs/math_path/1.00/schemas/record.xsd",
				"record");
				
			registerSchema ("td_demo",
				"file:///C:/Documents and Settings/ostwald/devel/projects/ncs/td_demo/1.00/schemas/record.xsd",
				"record");
				
			registerSchema ("vocab",
				"http://test.nldr.library.ucar.edu/metadata/vocabs/vocabs.xsd",
				"term");

			registerSchema ("vocabs",
				"http://test.nldr.library.ucar.edu/metadata/vocabs/vocabs.xsd",
				"vocabTerm");
				
			registerSchema ("lar_prod",
				"http://ns.nsdldev.org/ncs/lar/1.00/schemas/lar.xsd",
				"record");	

			registerSchema ("lar",
				// "C:/Users/ostwald/devel/projects/nsdl-schemas-project/ncs/lar/1.00/schemas/lar.xsd",
				// "C:/Users/ostwald/devel/projects/nsdl-schemas-project/ncs/lar/1.00/schemas/lar-previous.xsd",
				"http://ns.nsdldev.org/ncs/lar/1.00/schemas/lar.xsd",
				"record");	
			registerSchema ("cow_item",
				"http://cow.lhs.berkeley.edu/metadata/cowItem/1.0rc3/cowItem.xsd",
				"record");					
		}
		return schemaMap;
	}
						   

	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		// System.out.println("SchemaRegistry: " + s);
		System.out.println(s);
	}
	
	public class Schema {
		public String name;
		public String path;
		public String rootElementName;
		
		public Schema (String name, String path, String rootElementName) {
			this.name = name;
			this.path = path;
			this.rootElementName = rootElementName;
		}
	}
	
}

