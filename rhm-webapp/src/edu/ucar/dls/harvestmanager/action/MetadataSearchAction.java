package edu.ucar.dls.harvestmanager.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.ucar.dls.harvest.tests.LRTests;
import edu.ucar.dls.harvestmanager.HarvestManager;
import edu.ucar.dls.harvestmanager.MetadataDAO;
import edu.ucar.dls.harvestmanager.action.form.MetadataSearchForm;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

/**
 *  Action controller for the Searching for Harvested Records in the DB
 */
public final class MetadataSearchAction extends Action {
	
	// There are 3 queries to be able to do this.
	
	
	// This is used when no resource info is needed or filtered on. Since this will 
	// be the fastest way to search for records
	public static String searchQueryWithoutResource = 
		"SELECT metadatahandle, partnerid, nativeformat, targetformat, target_xml, created_date," +
		    "setSpec, sessionid " +
		"FROM metadata " +
		"WHERE %s " +
		"LIMIT ?, ?";
	
	// This is the case when when you are searching on something besides resource data, 
	// it must be a left outer join since all comm_para and comm_anno doesn't have 
	// resource data
	public static String searchQueryWithResource = 
		"SELECT metadata.metadatahandle, partnerid, nativeformat, targetformat, metadata.created_date," +
		    "resource.url, resource.resourcehandle, target_xml, metadata.setSpec, "+
		    "metadata.sessionid "+
		"FROM metadata " +
		"LEFT OUTER JOIN resource on metadata.metadatahandle=resource.metadatahandle and metadata.setSpec=resource.setSpec " +
		               " and metadata.sessionid=resource.sessionid "+
		"WHERE %s " +
		"LIMIT ?, ?";
	
	// Finally this query is used when we are searching by a resource param like url or
	// resourcehandle. In this case no reason to do a left outer join.
	public static String searchQueryOnResource = 
		"SELECT metadata.metadatahandle, partnerid, nativeformat, targetformat, metadata.created_date," +
		    "resource.url, resource.resourcehandle, target_xml, metadata.setSpec, "+
		    "metadata.sessionid "+
		"FROM metadata, resource " +
		"WHERE %s and metadata.metadatahandle=resource.metadatahandle and metadata.setSpec=resource.setSpec " +
		               " and metadata.sessionid=resource.sessionid " +
		"LIMIT ?, ?";
	
	// Since limits are used on the queries above, we also need to the total count to show,
	// this is used when resouse data is not being used to filter on
	public static final String TOTAL_COUNT_QUERY = 
		"SELECT count(*) as count "+
		"FROM metadata " +
		"WHERE %s ";
	
	// In this case resource data is being filtered on
	public static final String TOTAL_COUNT_QUERY_ON_RESOURCE = 
		"SELECT count(*) as  count "+
		"FROM metadata, resource " +
		"WHERE %s and metadata.metadatahandle=resource.metadatahandle and metadata.setSpec=resource.setSpec " +
		               " and metadata.sessionid=resource.sessionid ";

	public static final int DEFAULT_RESULTS_PER_PAGE = 20;
	
	// Like statement to enable wild card searching
	public static final String LIKE_STMT = "CONVERT(%s USING latin1) like concat('%%',?,'%%')";
	public static final String EXACT_STMT = "%s=?";
	
	// For searching for setSpecs
	public static final String SET_SPEC_EQUALS_STMT = "metadata.setSpec=?";
	
	/**
	 * Execute method for the action
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) 
        throws Exception {
		
		Connection connection = this.getConnection();
		MetadataSearchForm searchForm = (MetadataSearchForm)form;

		// special cases for overriding form in session
		String clearParam = request.getParameter("clear");
		String[] setSpecParams = request.getParameterValues("setSpec");

		if(clearParam!=null && clearParam.equals("true") )
		{
			// We only want to reset the form when clear is sent in
			// So we setReset to true then call reset
			searchForm.setReset(true);
			searchForm.reset(mapping, request);
		}
		if(setSpecParams!=null)
		{
			// Special case during a reset from outside the 
			// page of setting the setSpec correctly
			searchForm.setSetSpec(setSpecParams);
		}
		
		// By default we select target as the default
		if(searchForm.getSearchOver()==null)
		{
			searchForm.setSearchOver("target");
		}
		
		String[] setSpecSelections = searchForm.getSetSpec();
		String keyword = searchForm.getKeyword();
		String searchOver = searchForm.getSearchOver();
		String pageObject = (String)request.getParameter("page");

		int page = 1;
		if(pageObject!=null)
		{
			String pageString = (String)pageObject;
			try
			{
				page = Integer.parseInt(pageString);
			}
			catch (NumberFormatException e)
			{
				// No need to do anything, One must be forging the page, default is first page
				
			}
		}

		
		int resultsPerPage = DEFAULT_RESULTS_PER_PAGE;
		
		try
		{
			String numberPerPageString = searchForm.getResultsPerPage();
			if(numberPerPageString!=null)
				resultsPerPage = Integer.parseInt(numberPerPageString);
		}
		catch(NumberFormatException e)
		{
			// no need to do anything, since someone is forging results per page, default is 
			// just default
		}
		searchForm.setResultsPerPage(String.valueOf(resultsPerPage));
		
		// Need to figure out the bottom range for the record results
		int lowLimit = 0;
		if(page>0)
		{
			lowLimit = (page-1) * resultsPerPage;
		}

		ArrayList<String> stringParams = new ArrayList<String>();
		ArrayList<String> whereList = new ArrayList<String>();
		whereList.add("1=1");
		boolean queryOnResource = false;
        boolean doGetExactCount = false;
		
		// If keyword is specified, here we go through all the search over and correctly
		// assign what the statement should be
		if(keyword!=null && keyword!="")
		{
			String columnName = null;
			String stmt = null;
			
			if(searchOver.equals("native"))
			{
				columnName = "native_xml";
				stmt = LIKE_STMT;
			}
			else if(searchOver.equals("target"))
			{
				columnName = "target_xml";
				stmt = LIKE_STMT;
			}
			else if(searchOver.equals("metadatahandle"))
			{
				columnName = "metadata.metadatahandle";
				stmt = EXACT_STMT;
			}
			else if(searchOver.equals("resourcehandle"))
			{
				queryOnResource = true;
				columnName = "resource.resourcehandle";
				stmt = EXACT_STMT;
			}
			else if(searchOver.equals("partnerid"))
			{
				columnName = "metadata.partnerid";
				stmt = EXACT_STMT;
			}
			else if(searchOver.equals("resourceurl"))
			{
				queryOnResource = true;
				columnName = "resource.url";
				stmt = EXACT_STMT;
			}
			
			// As long as they were set, set it in the where statement
			// along with adding the keyword to the string params
			if(columnName!=null && stmt!=null)
			{
				whereList.add(String.format(stmt, columnName));
				stringParams.add(keyword);
			}
			
		}
		
		// Now we add the setSpecs if they were selected
		if (setSpecSelections!=null && setSpecSelections.length>0)
		{
            doGetExactCount = true;

			ArrayList<String> whereseSetSpecList = new ArrayList<String>();
			
			for(String setSpec:setSpecSelections)
			{
				whereseSetSpecList.add(SET_SPEC_EQUALS_STMT);
				stringParams.add(setSpec);
			
			}
			
			// The setspecs are an or
			if(whereseSetSpecList.size()>0)
				whereList.add("("+StringUtils.join(whereseSetSpecList, " or ")+")");
		}
		
		String metadataQuery = null;
		String totalCountQuery = null;
		
		// Per the comments above describing why we need different queries for efficiency
		// this is picking out the correct one.
		if(queryOnResource)
		{
			metadataQuery = searchQueryOnResource;
			totalCountQuery = TOTAL_COUNT_QUERY_ON_RESOURCE;
			
			// If we query on resourse data might as well show the data since we have
			// it
			searchForm.setShowResourceInfo("true");
		}
		else if(searchForm.getEvalShowResourceInfo())
		{
			metadataQuery = searchQueryWithResource;
			totalCountQuery = TOTAL_COUNT_QUERY;
		}
		else
		{
			metadataQuery = searchQueryWithoutResource;
			totalCountQuery = TOTAL_COUNT_QUERY;
		}
		PreparedStatement metadataPstmt = connection.prepareStatement(
				String.format(metadataQuery, StringUtils.join(whereList, " and ")));
		
		PreparedStatement totalCountPstmt = connection.prepareStatement(
				String.format(totalCountQuery, StringUtils.join(whereList, " and ")));
		
		// Set all the params into the prepared statements
		int index = 1;
		for(String stringParam:stringParams)
		{	
			metadataPstmt.setString(index, stringParam);
			totalCountPstmt.setString(index, stringParam);
			index++;
		}
		metadataPstmt.setInt(index, lowLimit);
		index++;
		metadataPstmt.setInt(index, resultsPerPage);

		System.out.println("MetadataSearchAction SQL metadataPstmt: '" + metadataPstmt + "'");

		ResultSet rs = metadataPstmt.executeQuery();

        System.out.println("MetadataSearchAction metadataPstmt.executeQuery() completed");
		
		// We need mappings from setSpec to collection name, these were saved in the 
		// harvest manager that is part of the servelet context. Get them from there
		ServletContext sc=getServlet().getServletContext();
		HarvestManager hm = (HarvestManager)sc.getAttribute("harvestManager");
		
		Map<String, String> collectionNameMangger = hm.getCollectionNameMappings();
		
		// Create beans for each record, Sort of what hibernate could have done
		ArrayList<MetadataDAO> metadata = new ArrayList<MetadataDAO>();
		while(rs.next())
			metadata.add(new MetadataDAO(rs, collectionNameMangger, searchForm.getEvalShowResourceInfo()));
		
		// close first query
		rs.close();
		metadataPstmt.close();

        //System.out.println("MetadataSearchAction SQL totalCountPstmt: '" + totalCountPstmt + "'");

        int totalCount = 10000;

        // If we have a limited query, get the exact count (can time-out and fail if the query is unlimited):
        if(doGetExactCount) {

            rs = totalCountPstmt.executeQuery();

            //System.out.println("MetadataSearchAction totalCountPstmt.executeQuery() completed");

            rs.next();
            totalCount = rs.getInt("count");
            request.setAttribute("isExactCount", "true");
        }
        else {
            request.setAttribute("isExactCount", "false");
        }
		
		//close second query
		rs.close();
		totalCountPstmt.close();
		connection.close();

		// Finally we want this attributes on the page but not the form so set them in request
		request.setAttribute("metadataResults", metadata);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("page", page);
		request.setAttribute("lowLimit", lowLimit);

		return mapping.findForward("success");
	}
	
	/**
	 * Get the connection via the servelet params
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private Connection getConnection() throws ClassNotFoundException, SQLException
	{
		Connection connection = null;	  
		ServletContext sc=getServlet().getServletContext();

		String harvestDbUser=sc.getInitParameter("harvestDbUser");
        String harvestDbPwd=sc.getInitParameter("harvestDbPwd");
        String harvestDbUrl=sc.getInitParameter("harvestDbUrl")+"/hm_repository";
        
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(harvestDbUrl, 
				harvestDbUser,
				harvestDbPwd);
		return connection;
	}


}


