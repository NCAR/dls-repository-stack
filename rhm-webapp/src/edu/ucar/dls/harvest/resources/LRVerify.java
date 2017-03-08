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
package edu.ucar.dls.harvest.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ardverk.coding.BencodingOutputStream;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.util.URLConnectionTimedOutException;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.InvalidSignatureException;


/**
 * Utilities for verifying signed messages
 *
 * @version 0.1.1
 * @since 2011-12-08
 * @author Todd Brown / Navigation North
 *      <br>
 *      Copyright 2011 Navigation North Learning Solutions LLC
 *      <br>
 *      Licensed under the Apache License, Version 2.0 (the "License"); See LICENSE
 *      and README.md files distributed with this work for additional information
 *      regarding copyright ownership.
 */
public class LRVerify
{
	private int SERVER_REQUEST_TIMEOUT=10000;
	private List<Map<String, Object>> publicKeys = null;
	
	public LRVerify(List<Map<String, Object>> publicKeys) throws Exception
	{
		if(publicKeys==null || publicKeys.size()==0)
			throw new Exception("At least one public key is required to verify a document");
		
		this.publicKeys = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> publicKey: publicKeys)
		{
			if(!publicKey.containsKey("url"))
				continue;
			try {

				String publicKeyUrlResponse = TimedURLConnection.importURL(
								(String)publicKey.get("url"), Config.ENCODING, 
						SERVER_REQUEST_TIMEOUT);
				publicKey.put("publicKeyUrlResponse", publicKeyUrlResponse);
				this.publicKeys.add(publicKey);
			} catch (IOException e) {
				// can have multiple keys. hopefully the other ones are valid
			} catch (URLConnectionTimedOutException e) {
				// can have multiple keys. hopefully the other ones are valid
			}
		}
		
		if(this.publicKeys.size()==0)
			throw new Exception("At least one VALID public key is required to verify a document. "+
					"All the defined ones do not exist.");
		
	}
	/**
     * Converts input strings to input streams for the main verify function
     *
     * @param signature String of the signature
     * @param message String of the message
     * @param publicKey String of the public key
	 * @return true if signing is verified, false if not
	 * @throws LRException NULL_FIELD if any field is null, INPUT_STREAM_FAILED if any field cannot be converted to an input stream
     */
	public boolean verify(Map lrDocument) throws Exception
	{

		Map digitalSignatureJson = (Map)lrDocument.get(
		"digital_signature");
		
		String signature = (String)digitalSignatureJson.get("signature");
		
		
		if (signature == null || lrDocument == null)
			return false;
		
		// Check that none of the inputs are null
		if (this.publicKeys.size() == 0)
		{
			throw new Exception("PublicKeys must not be empty");
		}

		signature = this.removeSignatureHeaderFooter(signature);
		// Convert all inputs into input streams
		String hashedEnvelope = null;
		InputStream isPublicKey = null;
		
		try
		{
			hashedEnvelope = hashDocumentEnvelope(lrDocument);
		}
		catch (Exception e)
		{
		}
		
		Exception exceptionToRaise = null;
		for(Map<String, Object> publicKey: this.publicKeys)
		{
			String publicKeyUrlResponse = (String)publicKey.get("publicKeyUrlResponse");
			boolean validateHash = ((Boolean)publicKey.get("checkHash")).booleanValue();
			
			
			isPublicKey = new ByteArrayInputStream(publicKeyUrlResponse.getBytes());
			// Feed the input streams into the primary verify function
			
			try
			{
				boolean response =  verify(signature, isPublicKey, validateHash, 
						hashedEnvelope);
				if(response)
					return true;
			}
			catch(Exception e)
			{
				exceptionToRaise = e;
			}
		}
		if(exceptionToRaise!=null)
			throw exceptionToRaise;
		return false;
	}

	
	/**
	 * Verfies that the provided message and signature using the public key
	 *
	 * @param isSignature InputStream of the signature
	 * @param isMessage InputStream of the message
	 * @param isPublicKey InputStream of the public key
	 * @throws Exception 
	 * @throws LRException
	 */
    private boolean verify(String signature, InputStream isPublicKey, boolean validateHash,
    		String hashedEnvelope) throws InvalidSignatureException
    {
    	InputStream isSignature = new ByteArrayInputStream(signature.getBytes());
		// Get the public key ring collection from the public key input stream
		PGPPublicKeyRingCollection pgpRings = null;
		
		try
		{
			 pgpRings = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(isPublicKey));
		}
		catch (Exception e)
		{
			throw new InvalidSignatureException("The public key stream does not contain a valid public key.");
		}
	
		// Add the Bouncy Castle security provider
		Security.addProvider(new BouncyCastleProvider());
		
		// Build an object factory from the signature input stream and try to get an object out of it
		Object o = null;
		try
		{
			PGPObjectFactory pgpFact = new PGPObjectFactory(PGPUtil.getDecoderStream(isSignature));
			o = pgpFact.nextObject();
		}
		catch (Exception e)
		{
			throw new InvalidSignatureException("The signature stream does not contain a valid signature.");
		}
			
		
		// Check if the object we fetched is a signature list and if it is, get the signature and use it to verfiy
		try
		{	
			
			if (o instanceof PGPSignatureList)
			{
				PGPSignatureList list = (PGPSignatureList)o;
				if (list.size() > 0)
				{
					PGPSignature sig = list.get(0);
					
					PGPPublicKey publicKey = pgpRings.getPublicKey(sig.getKeyID());
					
					if(publicKey==null)
						throw new InvalidSignatureException("A signature matching the provided key could not be found."+
							"Either the public key has been changed or someone is forging the signature.");
					
					if(validateHash)
					{
						
						String verifiedHash = extractHashFromSignature(signature);
						if(!verifiedHash.equals(hashedEnvelope))
							throw new InvalidSignatureException("A signature matching the provided key was found, but "+
									"the verified hash does not match the hashed envolope. So either the envolope was "+
									"changed after it was signed or LRSignature's algorithm was changed and ours needs," +
									"to be updated too.");
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new InvalidSignatureException("A signature matching the provided key could not be found."+
					"Either the public key has been changed or someone is forging the signature.");
		}
		
		// If sig.verify throws an Exception that means the key was wrong. If it returns false that
		// means that the envelope hash doesn't match, that is why if we get here. it already 
		// validated the signature to the public key. Therefore we can return true if one isn't
		// validating the msgHash

		return true;
	}
    
    
    private String removeSignatureHeaderFooter(String signature) {
		return signature.replace("-----BEGIN PGP SIGNED MESSAGE-----", "").replace(
				"-----END PGP SIGNATURE-----", "");
	}
    
    
    private String extractHashFromSignature(String signature) {

		ArrayList<String> msgList = new ArrayList<String>(
				Arrays.asList(signature.split("\r\n|\r|\n")));
		Iterator<String> msgListIterator = msgList.iterator();
		
		// First remove the header of the signature
		int status = 0;
		while(msgListIterator.hasNext())
		{	
			String msgLine = msgListIterator.next();
			if (msgLine.matches("^[^:]+: .+$"))
            {
                status = 1;
            }
            else if ((status == 1) && msgLine.equals(""))
                status = 2;
			
			msgListIterator.remove();
            
            if (status == 2)
            {
                break;
            }
		}
		
		// next take all the lines until the signature starts
		String hash = "";
		msgListIterator = msgList.iterator();
		while(msgListIterator.hasNext())
		{
			String msgLine = msgListIterator.next();
			if (msgLine.matches("^-----BEGIN PGP SIGNATURE-----$"))
                break;
			hash+=msgLine;
		}
		
		return hash;
	}
    
	private static String hashDocumentEnvelope(Map lrDocument) throws Exception
    {
    	Map<String, Object> signableEnvolopeMap = LRVerify.stripEnvelope(lrDocument);
    	signableEnvolopeMap  = (Map<String, Object>)LRVerify.bNormalize(signableEnvolopeMap);
    	String envelope = LRVerify.bencode(signableEnvolopeMap);
    	return envelope; 
    }
    
    private static Object bNormalize(Object obj) {
		if (obj==null)
			return "null";
		else if (obj instanceof Float)
			return ((Float)obj).toString();
		else if ( obj instanceof Boolean)
			return ((Boolean)obj).toString();
		else if (obj instanceof List)
		{
            List<Object> normalizedList = new ArrayList();
            for (Object child:(List<Object>)obj)
            	normalizedList.add(bNormalize(child));
            return normalizedList;
		}
		else if(obj instanceof Map)
        {
        	ConcurrentHashMap<String, Object> concurrentMap = 
        			new ConcurrentHashMap<String, Object>((Map<String, Object>)obj);
        	Iterator<Map.Entry<String, Object>> mapIterator = concurrentMap.entrySet().iterator();
        	
    		while (mapIterator.hasNext()) {
    			
    			Map.Entry pairs = mapIterator.next();
    			
    			Object value = pairs.getValue();
    			Object normalizedValue = bNormalize(value);
    			concurrentMap.put((String)pairs.getKey(), normalizedValue);
    		}
    		return concurrentMap;
        }
    
        return obj;		
	}
    
	private static Map<String, Object> stripEnvelope(Map lrDocument)
    {
		
    	String [] excludeFields = {"digital_signature", "publishing_node", "update_timestamp", 
    			"node_timestamp", "create_timestamp", "doc_ID", "_id", "_rev"};
    	List<String> excludeFieldsList = Arrays.asList(excludeFields);
    	
    	HashMap signableLRDocument = new HashMap();
    	
    	Iterator<Map.Entry> entryIterator = lrDocument.entrySet().iterator();
		
		while (entryIterator.hasNext()) {
			
			Map.Entry pairs = entryIterator.next();
			
			if (!excludeFieldsList.contains(pairs.getKey()))
			{
				
				signableLRDocument.put(pairs.getKey(), pairs.getValue());
			}
		}
    	return signableLRDocument;
    }
    
    /**
     * Bencodes document
     *
     * @param doc Document to be bencoded
     * @return Bencoded string of the provided document
     * @throws Exception 
     * @throws LRException BENCODE_FAILED if document cannot be bencoded
    */
    private static String bencode(Map<String, Object> doc) throws Exception
    {
        String text = "";
        String encodedString = "";
        
        // Bencode the provided document
        
        try
        {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            BencodingOutputStream bencoder = new BencodingOutputStream(s);
            bencoder.writeMap(doc);
            bencoder.flush();
            encodedString = s.toString();
            
            
            s.close();

            // Hash the bencoded document
            MessageDigest md;
            
            md = MessageDigest.getInstance("SHA-256");
                
            md.update(encodedString.getBytes());
            byte[] mdbytes = md.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<mdbytes.length; i++)
            {
                String hex = Integer.toHexString(0xFF & mdbytes[i]);
                if (hex.length() == 1)
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            text = hexString.toString();
        }
        catch (Exception e)
        {
            throw new Exception("The document could not be bencoded. The resource data may be an invalid structure.");
        }
        
        return text;
    }
    
}
