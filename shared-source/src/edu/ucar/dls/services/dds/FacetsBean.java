package edu.ucar.dls.services.dds;

import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.index.*;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermsFilter;

import java.util.*;

/**
 *  Class to implement quick and dirty faceted search.
 *
 *@author    ostwald
 */
public class FacetsBean {
	private static boolean debug = false;

	private BooleanQuery booleanQuery = null;
	private Filter existingFilter = null;
	private RepositoryManager rm = null;
	private Map facetMap = null;
	private SimpleLuceneIndex index = null;
	private QueryParser qp = null;
	private String[] facetFields = null;
	private boolean generateAlways = false;


	/**
	 *  soon we will add field list to params
	 *
	 *@param  booleanQuery  A query used to obain un-faceted results from index
	 *@param  facetFields   Fields for which to compute facet info
	 *@param  rm            repository manager
	 */
	public FacetsBean(BooleanQuery booleanQuery, Filter existingFilter, String[] facetFields, RepositoryManager rm) {
		this.booleanQuery = booleanQuery;
		this.existingFilter = existingFilter;
		this.facetFields = facetFields;
		this.rm = rm;
		this.index = rm.getIndex();
		this.qp = index.getQueryParser();
		this.facetMap = this.getFacetMap();
		this.generateAlways = true;
	}


	/**
	 *  The facetMap keys are facetFields (index fields) and values are termMaps
	 *
	 *@return    The facetMap value
	 */
	public Map getFacetMap() {
		if (this.facetMap == null || generateAlways) {
			prtln("\n deriving facet map");
			this.facetMap = new TreeMap();

			// a termMap is built for each facetField
			for (int i = 0; i < facetFields.length; i++) {
				String field = facetFields[i];

				Map termMap = getTermMap(field);

				prtln(" term Map has " + termMap.size() + " entries");

				// put termMap into the facetMap
				this.facetMap.put(field, termMap);
			}
		}
		return this.facetMap;
	}


	/**
	 *  The termMap keys are facetField terms, and the values are the occurrances
	 *  of terms
	 *
	 *@param  field  a facetField (aka indexed field)
	 *@return        The termMap value
	 */
	Map getTermMap(String field) {
		Map termMap = new TreeMap();
		// get a  count for each term of termList

		// prtln ("getTermMap() field: " + field);
		List termList = this.index.getTerms(field);

		/*
		 *  Note: termlist contains INDEX-WIDE occurances for a given term,
		 *  so we don't add any 0-size results to the term list
		 */
		for (Iterator termIter = termList.iterator(); termIter.hasNext(); ) {
			String term = (String) termIter.next();
			// prtln("  term: " + term);
			
			Filter finalFilter = null;
			
			// Query method:
			//Query query = getFacetQuery(field, term);
			//ResultDocList results = rm.getIndex().searchDocs(query);
			
			// Filter method:
			TermsFilter termsFilter = new TermsFilter();
			termsFilter.addTerm(new Term(field,term));
			
			if(this.existingFilter != null) {
				BooleanFilter booleanFilter = new BooleanFilter();
				booleanFilter.add(new FilterClause(termsFilter,BooleanClause.Occur.MUST));
				booleanFilter.add(new FilterClause(this.existingFilter,BooleanClause.Occur.MUST));
				finalFilter = booleanFilter;
			}
			else {
				finalFilter = termsFilter;
			}
			
			ResultDocList results = rm.getIndex().searchDocs(this.booleanQuery,finalFilter);
			if (results != null && !results.isEmpty())
				termMap.put(term, results.size());
			else
				termMap.put(term, 0);
		}
		return termMap;
	}


	/**
	 *  Returns a query that addes a clause to this FacetBean's booleanQuery
	 *  attribute to restrict the query to find records having specified term in
	 *  specified field. T
	 *
	 *@param  field  facetfield (the indexed field)
	 *@param  term   the term for which we will search
	 *@return        the query
	 */
	Query getFacetQuery(String field, String term) {
		// prtln ("\nterm: " + term);
		Query termQuery = null;

		String termQueryStr = null;

		termQueryStr = field + ":" +
				edu.ucar.dls.schemedit.SchemEditUtils.quoteWrap(term);
		try {
			termQuery = qp.parse(termQueryStr);

		} catch (org.apache.lucene.queryParser.ParseException pe) {
			/* NOTE: I'm actually not sure how to handle this exeption ... 
			need to think about it
			*/
			prtlnErr("getFacets parse Exception: " + pe.getMessage());
			return termQuery;
		}

		BooleanQuery query = (BooleanQuery) booleanQuery.clone();
		if (termQueryStr != null) {
			query.add(termQuery, BooleanClause.Occur.MUST);
		}

		return query;
	}


	/**
	 *  debugging
	 */
	public void showFacetMap() {
		if (facetMap != null) {
			prtln("faceMap has " + facetMap.size() + " entries");
			// debugging - show the map of maps
			prtln("\n\n------------\nFacet Map");
			for (Iterator fieldIter = facetMap.keySet().iterator(); fieldIter.hasNext(); ) {

				String field = (String) fieldIter.next();
				prtln("- field: " + field);
				Map termMap = (Map) facetMap.get(field);

				for (Iterator termIter = termMap.keySet().iterator(); termIter.hasNext(); ) {
					String term = (String) termIter.next();
					int numHits = (Integer) termMap.get(term);
					prtln("\t" + term + ": " + numHits);
				}
			}
		} else {
			prtln("facetMap is NULL");
		}
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 *@param  s  The text that will be output to error out.
	 */
	protected final void prtlnErr(String s) {
		System.out.println(s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
}

