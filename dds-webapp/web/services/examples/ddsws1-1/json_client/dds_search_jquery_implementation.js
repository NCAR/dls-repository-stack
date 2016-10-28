/* 
 This script implements a simple search page for a DDS or DCS repository.

 Requires:
 dds_search.html - The HTML portion of the implementaion
 jquery-1.x.js - JQuery JavaScript framework library

 Digital Learning Sciences (DLS)
 University Corporation for Atmospheric Research (UCAR)
 P.O. Box 3000
 Boulder, Colorado 80307 USA
 http://dlsciences.org/

 This code may be copied and modified, free and with no warranty.
 */

var json = null;
var numResults = 10;

var ajaxTimeout = 30000; // 30 second timeout to detect API outages and errors and display a message to the user.

var param_action = getParameterByName("action");
var param_q = getParameterByName("q");
var param_s = getParameterByName("s");
var param_sl = getParameterByName("sl");
var param_field = getParameterByName("field");

var smartLinkQueries = {};
var smartLinkBooserTerms = {};
var smartLinkLabels = {};
var smartLinkNum = 0;

if(!baseUrl)
    var baseUrl = '@DDS_SERVER_BASE_URL@/services/ddsws1-1';

$( document ).ready(function() {
    onPageLoad();
});

var DDSSearch = {
    // Define a smart link, mapped to an http parameter:
    defineSmartLink: function(sl_httpParm, sl_label, sl_query, sl_boost_terms) {
        if(sl_httpParm && sl_label && sl_query){
            smartLinkQueries[sl_httpParm] = sl_query;
            smartLinkBooserTerms[sl_httpParm] = sl_boost_terms;
            smartLinkLabels[sl_httpParm] = sl_label;
        }
    }
}

function onPageLoad () {
    // Load smart links that are defined on <a> elements:
    $('.smartLink').each(function(i, elm) {
        var smParam = 'sl' + (++smartLinkNum);
        var label = elm.innerHTML;
        //log("elm:" + label);
        if(elm.getAttribute('altLabel') != null)
            label = elm.getAttribute('altLabel');
        if(label.length == 0)
            label = ' ';
        var query = elm.getAttribute('query');
        var boostTerms = elm.getAttribute('boost')
        if(boostTerms)
            boostTerms = boostTerms.split(",");
        //log('define smartLink: httpParam:' + smParam + ' label:' + label + ' query:' + query + ' boostTerms:' + boostTerms);
        elm.setAttribute('href','?sl='+smParam);
        DDSSearch.defineSmartLink(smParam,label,query,boostTerms);
    });

    // Check if this is a Search action:
    if(!param_action && (param_q != null || param_sl))
        param_action = 'Search';

    if(typeof(param_action) != 'undefined') {
        doServiceRequest(param_action);
    }

    // Set browser focus to the search box:
    if( $('#searchFormQuery') )
        $('#searchFormQuery').focus();

    if(typeof(showDeveloperInfo) != 'undefined' && showDeveloperInfo && $('showDeveloperInfo'))
        $('#showDeveloperInfo').show();
}

function doServiceRequest(action) {
    if(typeof(action) != 'string')
        return;

    var request = null;

    if(action == 'Search') {
        var userQuery = getUserQuery();
        var s = param_s;
        if(!s) s = 0;

        //var isSmartLinkQuery = false;
        if(param_sl && typeof(smartLinkQueries) != 'undefined') {
            //isSmartLinkQuery = true;
            var smartLinkQuery = '(' + smartLinkQueries[param_sl] + ')';
            var boosterTerms = smartLinkBooserTerms[param_sl];
            if(boosterTerms) {
                smartLinkQuery += ' AND ' + makeBooserQueryClause(boosterTerms);
            }
        }

        userQuery = userQuery.replace(/\+/g, ' ').replace(/\?/g, ' ').trim();
        //log('userQuery:"' + userQuery + '" length:' + userQuery.length + 'isSmartLink' + (!smartLinkQuery));
        if(userQuery.length == 0 && !smartLinkQuery) {
            $('#searchResults').html('<p>You did not define a search. Please enter one or more keywords in the text box above. </p>');
            return;
        }

        if(userQuery.length > 0 && typeof(expandAndBoostSearch) != 'undefined' && expandAndBoostSearch)
            userQuery = '(('+userQuery+') OR title:('+userQuery+') OR stems:('+userQuery+') OR titlestems:('+userQuery+'))';

        var query = '('+userQuery+')';

        if(smartLinkQuery) {
            if(userQuery.length > 0)
                query = '('+userQuery+') AND ('+smartLinkQuery+')';
            else
                query = '('+smartLinkQuery+')';
        }

        if(typeof(collections) != 'undefined')
            query += ' AND ky:('+makeBooleanClause(collections,'OR')+')';

        if(typeof(xmlFormats) != 'undefined')
            query += ' AND xmlFormat:('+makeBooleanClause(xmlFormats,'OR')+')';

        if(typeof(collections) != 'undefined')
            query += ' AND ky:('+makeBooleanClause(collections,'OR')+')';

        if(typeof(virtualCollectionFilter) != 'undefined')
            query += ' AND ('+virtualCollectionFilter+')';

        if(typeof(omitCollectionRecords) != 'undefined' && omitCollectionRecords == true)
            query += ' AND !xmlFormat:dlese_collect';

        var sort = '';
        if(typeof(sortAscendingBy) != 'undefined' && typeof(sortDescendingBy) != 'undefined')
            log('Client code error: You may specify sortAscendingBy -or- sortDescendingBy but not both. Ignoring...');
        else if (typeof(sortAscendingBy) != 'undefined')
            sort = '&sortAscendingBy='+encodeURIComponent(sortAscendingBy);
        else if (typeof(sortDescendingBy) != 'undefined')
            sort = '&sortDescendingBy='+encodeURIComponent(sortDescendingBy);

        // Request stored content for standard fields:
        var storedContentArgs = '&storedContent=title&storedContent=description&storedContent=url';

        // Construct the request to the service:
        request = baseUrl + '?verb=Search&client=js_client&output=json'+sort+'&transform=localize&s='+s+'&n='+numResults+storedContentArgs+'&q='+encodeURIComponent(query);
    }
    else if(action.startsWith('List') || action == 'ServiceInfo') {

        // Construct the request to the service:
        request = baseUrl + '?verb='+action+'&client=js_client&output=json&transform=localize';

        if(action == 'ListTerms' && param_field)
            request += '&field='+encodeURIComponent(param_field);
    }

    if(request == null)
        return;

    if(typeof(showDeveloperInfo) != 'undefined' && showDeveloperInfo) {
        if($('#devRequestUrl'))
            $('#devRequestUrl').show().attr('href', request);
    }

    $('#loadMsg').show();


    // Get the search results as JSON, which calls renderResponse() on return
    fetchJson(request);
}

// This function boosts ranking of results that match the given terms or phrases without changing the result set itself - AND this with other clauses:
function makeBooserQueryClause(bt) {
    return '(allrecords:true OR title:('+makeBooleanClause(bt,"OR")+')^3 OR titlestems:('+makeBooleanClause(bt,"OR")+')^3 OR description:('+makeBooleanClause(bt,"OR")+')^3)';
}

// Render the response:
function renderResponse(json) {
    this.json = json;
    try {
        if(json != null && json.DDSWebService) {

            // Handle case of errors, zero results:
            if(json.DDSWebService.error) {
                theHTML = '';
                var code = json.DDSWebService.error.code;
                if(code == 'noRecordsMatch') {
                    theHTML = 'There were no matches';
                    if(param_q)
                        theHTML += ' for <span class="queryText">' + param_q.replace(/\+/g, ' ') + '</span>';
                }
                else if(code == 'badQuery')
                    theHTML = 'There was a problem with the query syntax: ' + json.DDSWebService.error.content;
                else if(param_action == 'Search')
                    theHTML = 'The were no matches for your search.';
                else
                    theHTML = 'No information is available. <!-- Error: The service may be down or the request may be invalid. -->';

                $('#searchResults').html('<p>'+theHTML+'</p>');
                $('#loadMsg').hide();
                return;
            }
            // Handle display of Search results, if one or more records:
            else if( json.DDSWebService.Search && json.DDSWebService.Search.results && json.DDSWebService.Search.results.record ) {
                var searchHTML = '';
                var offset = Number(json.DDSWebService.Search.resultInfo.offset);
                var numReturned = Number(json.DDSWebService.Search.resultInfo.numReturned);
                var totalNumResults = Number(json.DDSWebService.Search.resultInfo.totalNumResults);

                // Create a pager
                if(param_sl && typeof(smartLinkLabels) != 'undefined')
                    var smartLinkLabel = smartLinkLabels[param_sl];

                var smartLinkLabelDisp = (smartLinkLabel ? ('<div class="smartLinkLabel">'+smartLinkLabel+'</div>') : '');
                var pager = '';
                var qArgs = '';
                if(param_q)
                    qArgs += 'q='+encodeURIComponent(param_q);
                if(param_sl)
                    qArgs += 'sl='+encodeURIComponent(param_sl);
                if(offset > 0)
                    pager += '<a href="?'+qArgs+'&s='+(offset-numResults)+'">&#171; Prev</a> - ';
                if(param_q)
                    pager += 'Results ';
                pager += (offset+1) + ' - ' + (offset+numReturned) + ' out of ' + formatNumber(totalNumResults);
                if( offset+numResults < totalNumResults)
                    pager += ' - <a href="?'+qArgs+'&s='+(offset+numResults)+'">Next &#187;</a>';
                pager = '<div class="pager">' + pager + '</div>';

                searchHTML += smartLinkLabelDisp + pager;

                var i = 0;
                // Handle multiple results:
                if( isArray(json.DDSWebService.Search.results.record) ) {
                    $.each(json.DDSWebService.Search.results.record, function(ii,record) {
                        searchHTML = searchHTML + getRecordDisplayHtml(record,i++);
                    });
                }
                // Handle single result:
                else {
                    searchHTML = searchHTML + getRecordDisplayHtml(json.DDSWebService.Search.results.record,i++);
                }

                searchHTML += pager;
                $('#searchResults').html('<p>'+searchHTML+'</p>');
                $('#loadMsg').hide();
                return;
            }
            /*
             Handle generic display of service responses (ListCollections, ListXmlFormats, ListFields, ServiceInfo)
             Disabled in JQuery version - use Prototype JS verstion to see these
             */
            else if(json.DDSWebService) {
                // The JsonViewerElement class is implemented in the prototype.js version only...
                $('#serviceResponse').html('<pre>' + JSONPrettyPrint(json.DDSWebService) + '</pre>');
                $('#serviceResponse').show();
                $('#loadMsg').hide();
                return;
            }
        }
    } catch (e) {
        var html='This browser appears not to support this operation. Message: ' + e;
        $('#searchResults').html('<p>'+html+'</p>');
        $('#loadMsg').hide();
        //throw e;
    }
    $('#loadMsg').hide();
}

function isArray(myVar){
    return (myVar && myVar.constructor === Array);
}

function isString(myVar){
    return (myVar && myVar.constructor === String);
}

function containsString(str1, str2, ignoreCase) {
    if(!isString(str1) || !isString(str2))
        return false;
    if(ignoreCase)
        return (str1.toLowerCase().indexOf(str2.toLowerCase()) >= 0);
    else
        return (str1.indexOf(str2) >= 0);
}

function isEmpty(myElm){
    return (!myElm || myElm.length == 0)
    //return ($.trim($(myElm).html())=='');
}

function stripScriptsAndTags(item){
    return $($.parseHTML(item)).text();
}

/* --
 Make a Lucene boolean search clause
 Args:
 arrayVar - an array object
 operator - one of 'AND' or 'OR'
 -- */
function makeBooleanClause(arrayVar, operator) {
    if(typeof(arrayVar) == 'string')
        return arrayVar;
    var clause = '';
    for(var i = 0; i < arrayVar.length; i++)
        clause += arrayVar[i] + (i == arrayVar.length-1 ? '' : (' '+operator+' '));
    return clause;
}

// Hack to decode/encode UTF-8 characters (helps clean-up some data):
function decodeUtf8( s ) {
    try {
        if (decodeURIComponent)
            return decodeURIComponent( escape(s) );
        else
            return s;
    } catch (e) {
        return s;
    }
}

function encodeUtf8( s ) {
    try {
        return unescape( encodeURIComponent(s) );
    } catch (e) {
        return s;
    }
}

function getRecordDisplayHtml(recordJson,i) {
    var html = '';
    try {
        var title = decodeUtf8( getRecordTitle(recordJson) );
        var description = decodeUtf8( getRecordDescription(recordJson) );
        var url = getRecordUrl(recordJson);
        var collectionDesc = stripScriptsAndTags(recordJson.head.collection.content);
        var recId = stripScriptsAndTags(recordJson.head.id);
        var standards = getRecordStandards(recordJson);
        var nsfAward = getNsfAwardInfo(recordJson);

        if(!isEmpty(title)) {
            title = title;
            title = hlKeywords(title);
            if(isEmpty(url))
                html += '<div class="recordTitle">' + title + '</div>';
            else
                html += '<div class="recordTitle"><a href="' + url + '">' + title + '</a></div>';
        }
        if(!isEmpty(url))
            html += '<div class="recordUrl">' + hlKeywords(url) + '</div>';
        if(isEmpty(title) && !isEmpty(url))
            html += '<div class="recordTitle"><a href="' + url + '">' + hlKeywords(url) + '</a></div>';
        if(!isEmpty(description)) {
            description = description;
            html += '<div class="recordDesc">' + hlKeywords(description) + '</div>';
        }
        if(!isEmpty(nsfAward))
            html += '<div class="subContent nsfAward">' + nsfAward + '</div>';
        if(!isEmpty(standards))
            html += '<div class="subContent standards">Aligned to: ' + standards + '</div>';
        if(!isEmpty(html)) {
            if(typeof(displayCollectionName) != 'undefined' && displayCollectionName)
                html += '<div class="subContent recordSource">From: ' + collectionDesc + '</div>';
            if(typeof(displayId) != 'undefined' && displayId)
                html += '<div class="subContent recordSource">ID: ' + recId + '</div>';
        }
    } catch (e) {
        log("Error extracting data: " + e);
        //throw e;
    }
    if(isEmpty(html)) {
        html += '<div class="recordTitle">Matching record: ID ' + recordJson.head.id + '</div>';
        html += '<div class="subContent recordSource">From: ' + recordJson.head.collection.content + '</div>';
    }
    if(typeof(displayFullRecordMetadata) != 'undefined' && displayFullRecordMetadata) {
        html += '<div class="jsonOpener"><span id="jsonOpener'+i+'" onclick="renderJsonTree('+i+')"><a href="javascript:void(0)" id="openerLnk'+i+'" class="openerLnk">+</a> <a href="javascript:void(0)" id="openerTxt'+i+'" class="openerTxt">View full record</a></span></div>';
        html += '<div class="jsonContainer" id="jsonContainer'+i+'" style="display:none"><pre>' + JSONPrettyPrint(recordJson.metadata) + '</pre></div>';
    }
    return '<div class="record">' + html + '</div>';
}

function hlKeywords(str) {
    if(!str || (typeof(highlightKeywords) != 'undefined' && !highlightKeywords)) return str;
    var userQuery = getUserQuery();
    //log("userQuery: " + userQuery);
    $(userQuery.split(/\W+/)).each(function(i,term){
        //log("term: " +term);
        if(!isStopWord(term) && $.trim(term).length > 0){
            var stem = stemmer(term);
            var pattern = new RegExp('((\\W|^)('+stem+'\\w*)(\\W|$))|((\\W|^)('+term+'\\w*)(\\W|$))','gi');
            str = str.replace( pattern, ('$2$6<em>$3$7</em>$4$8') );
        }
    });
    return str;
}

function JSONPrettyPrint(json){
    json = JSON.stringify(json, undefined, 2);
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    json = json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'json-number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'json-key';
            } else {
                cls = 'json-string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'json-boolean';
        } else if (/null/.test(match)) {
            cls = 'json-null';
        }
        return '<span class="' + cls + '">' + match.replace(/\":/g,' = ').replace(/\"/g,'') + '</span>';
    });
    if(typeof(showJsonBrackets) != 'undefined' && !showJsonBrackets)
        return json.replace(/{/g,'').replace(/\}/g,'').replace(/\[/g,'').replace(/\]/g,'').replace(/\]/g,'').replace(/>,/g,'>').replace(/= <\/span> \n/g,'\n').replace(/\n\s*\n/g,'\n').replace(/\n\s*,\n/g,'\n');
    else
        return json;
}

function isStopWord(term) {
    var isStopWord = false;
    $(ENGLISH_STOP_WORDS).each(function(i,sw){
        if(sw == term){
            isStopWord = true;
            return false;
        }
    });
    //log("isStopWord " + isStopWord + ": " + term);
    return isStopWord;
}

var ENGLISH_STOP_WORDS = [
    "a", "an", "and", "are", "as", "at", "be", "but", "by",
    "for", "if", "in", "into", "is", "it",
    "no", "not", "of", "on", "or", "s", "such",
    "t", "that", "the", "their", "then", "there", "these",
    "they", "this", "to", "was", "will", "with", "em"
];

function getUserQuery() {
    var userQuery = param_q;

    if(userQuery)
        $('#searchFormQuery').val(param_q.replace(/\+/g, ' '));
    else if( $('#searchFormQuery') )
        userQuery = $('#searchFormQuery').val().trim();
    else
        userQuery = '';
    return userQuery;
}

function getRecordTitle(recordJson) {
    try {
        // Standard stored content for title:
        var value = getStoredContent(recordJson,'title');
        if(value == null){
            // ADN record:
            if(recordJson.metadata.itemRecord)
                value = getContent(recordJson.metadata.itemRecord.general.title,1);
            // NCAR library_dc record:
            else if(recordJson.head.xmlFormat == 'library_dc')
                value = getContent(recordJson.metadata.record.title,1);
            // ncs_item, MSP2 record:
            else if(recordJson.metadata.record)
                value = getContent(recordJson.metadata.record.general.title,1);
            // DDS/DLESE Collection record:
            else if(recordJson.metadata.collectionRecord) {
                value = recordJson.metadata.collectionRecord.general.fullTitle;
                if(!value)
                    value = recordJson.metadata.collectionRecord.general.shortTitle;
            }
            // DLESE annotation record:
            else if(recordJson.metadata.annotationRecord)
                value = getContent(recordJson.metadata.annotationRecord.annotation.title,1);
            // DLESE news opps record:
            else if(recordJson.metadata['news-oppsRecord'])
                value = getContent(recordJson.metadata['news-oppsRecord'].title,1);
            // Concept record:
            else if(recordJson.metadata.concept)
                value = getContent(recordJson.metadata.concept.shortTitle,1);
            // OAI DC record:
            else if(recordJson.metadata.dc)
                value = getContent(recordJson.metadata.dc.title,1);
            // NSDL DC record:
            else if(recordJson.metadata.nsdl_dc)
                value = getContent(recordJson.metadata.nsdl_dc.title,1);
            // NSDL NCS Collection record:
            else if(recordJson.head.xmlFormat == 'ncs_collect')
                value = getContent(recordJson.metadata.record.general.title,1);
            // MODS record:
            else if(recordJson.metadata.mods) {
                value = getContent(recordJson.metadata.mods.titleInfo.title,1);
                if(value == '')
                    value = getContent(recordJson.metadata.mods.titleInfo[0].title,1);
            }
        }
        return (isString(value) ? value : '');
    } catch (e) {
        return '';
    }
}

function getRecordDescription(recordJson){
    try {
        // Standard stored content for description:
        var value = getStoredContent(recordJson,'description');
        if(value == null) {
            // ADN record:
            if(recordJson.metadata.itemRecord)
                value = getContent(recordJson.metadata.itemRecord.general.description);
            // NCAR library_dc record:
            else if(recordJson.head.xmlFormat == 'library_dc')
                value = getContent(recordJson.metadata.record.description);
            // ncs_item, MSP2 record:
            else if(recordJson.metadata.record)
                value = getContent(recordJson.metadata.record.general.description);
            // DLESE Collection record:
            else if(recordJson.metadata.collectionRecord)
                value = getContent(recordJson.metadata.collectionRecord.general.description);
            // DLESE annotation record:
            else if(recordJson.metadata.annotationRecord)
                value = getContent(recordJson.metadata.annotationRecord.annotation.content.description);
            // DLESE news opps record:
            else if(recordJson.metadata['news-oppsRecord'])
                value = getContent(recordJson.metadata['news-oppsRecord'].description);
            // Concept record:
            else if(recordJson.metadata.concept)
                value = getContent(recordJson.metadata.concept.longTitle);
            // OAI DC record:
            else if(recordJson.metadata.dc)
                value = getContent(recordJson.metadata.dc.description);
            // NSDL DC record:
            else if(recordJson.metadata.nsdl_dc)
                value = getContent(recordJson.metadata.nsdl_dc.description);
            // NSDL NCS Collection record:
            else if(recordJson.head.xmlFormat == 'ncs_collect')
                value = getContent(recordJson.metadata.record.general.description);
            // MODS record:
            else if(recordJson.metadata.mods) {
                value = getContent(recordJson.metadata.mods.note);
            }
        }
        return (isString(value) ? value : '');
    } catch (e) {
        return '';
    }
}


function getNsfAwardInfo(recordJson) {
    try {
        // Check if there is NSF award info:
        var value = null;
        if(value == null){
            // ADN record:
            if(recordJson.metadata.itemRecord){
                value = getContent(recordJson.metadata.itemRecord.general.additionalInfo,1);
                value = (containsString(value,"NSF") ? value : '');
            }
        }
        return (isString(value) ? value : '');
    } catch (e) {
        return '';
    }
}

var urlRegEx = /http(.)*|ftp(.)*/;

function getRecordUrl(recordJson){
    try {
        // Standard stored content for url:
        var value = getStoredContent(recordJson,'url');
        if(value == null) {
            // ADN record:
            if(recordJson.metadata.itemRecord)
                value = recordJson.metadata.itemRecord.technical.online.primaryURL;
            // NCAR library_dc record:
            else if(recordJson.head.xmlFormat == 'library_dc')
                value = getContent(recordJson.metadata.record.URL,1,urlRegEx);
            // ncs_item, MSP2 record:
            else if(recordJson.head.xmlFormat == 'msp2' || recordJson.head.xmlFormat == 'msp')
                value = getContent(recordJson.metadata.record.general.url,1,urlRegEx);
            // DLESE Collection record:
            else if(recordJson.metadata.collectionRecord)
                value = recordJson.metadata.collectionRecord.access.collectionLocation;
            // DLESE annotation record:
            else if(recordJson.metadata.annotationRecord)
                value = recordJson.metadata.annotationRecord.annotation.content.url;
            // DLESE news opps record:
            else if(recordJson.metadata['news-oppsRecord'])
                value = recordJson.metadata['news-oppsRecord'].announcementURL;
            // Concept record:
            else if(recordJson.metadata.concept)
                value = null;
            // OAI DC record:
            else if(recordJson.metadata.dc)
                value = getContent(recordJson.metadata.dc.identifier,1,urlRegEx);
            // NSDL DC record:
            else if(recordJson.metadata.nsdl_dc)
                value = getContent(recordJson.metadata.nsdl_dc.identifier,1,urlRegEx);
            // NSDL NCS Collection record:
            else if(recordJson.head.xmlFormat == 'ncs_collect')
                value = getContent(recordJson.metadata.record.general.url,1,urlRegEx);
            // OSM 1.1 (next):
            else if(recordJson.head.xmlFormat == 'osm' || recordJson.head.xmlFormat == 'osm_next')
                value = getContent(recordJson.metadata.record.resources.primaryAsset.url,1,urlRegEx);
            // MODS record:
            else if(recordJson.metadata.mods) {
                value = getContent(recordJson.metadata.mods.location.url,1,urlRegEx);
                if(value == '')
                    value = getContent(recordJson.metadata.mods.identifier,1,urlRegEx);
            }
        }
        return (isString(value) ? value : '');
    } catch (e) {
        log("getRecordUrl() error: " + e);
        return '';
    }
}



function getRelationUrlEntryItem(relation){
    var title = getContent(relation.urlEntry.title);
    var url = getContent(relation.urlEntry.url);

    if(title == undefined && url == undefined)
        return '';
    if(url == undefined)
        return title;
    if(title == undefined)
        title = url;

    return '<a href="' + url + '">' + hlKeywords(title) + '</a>';
}


function getRecordStandards(recordJson) {
    try {
        // Get a display of standards:
        if(value == null) {
            var value = '';

            // ADN record:
            if(recordJson.head.xmlFormat == 'adn') {
                // Standard in relatedResource

                var hasRelation = false;
                try {
                    hasRelation = typeof recordJson.metadata.itemRecord.relations.relation != 'undefined';
                } catch (e) {}

                if(hasRelation) {
                    if(isArray(recordJson.metadata.itemRecord.relations.relation)) {
                        var length = recordJson.metadata.itemRecord.relations.relation.length;
                        $.each(recordJson.metadata.itemRecord.relations.relation, function (i, relation) {
                            value += getRelationUrlEntryItem(relation);
                            if(!isEmpty(value) && i != length-1)
                                value += ", ";
                        });
                    }
                    else {
                        value = getRelationUrlEntryItem(recordJson.metadata.itemRecord.relations.relation);
                    }
                }
                return value;

            }
            // NSDL_DC:
            else if(recordJson.head.xmlFormat == 'nsdl_dc' || recordJson.head.xmlFormat == 'msp'){
                // Do something...
            }
        }
        return (isString(value) ? value : '');
    } catch (e) {
        log("getRecordStandards() error: " + e);
        return '';
    }
}

// Handle json paths for repeating fields, fields with content (e.g. xml attributes), dc variations:
function getContent(path,maxElms,matchesRegex) {
    try{
        var value = '';

        // If a single object:
        if(!path)
            value = '';
        else if(isString(path))
            value = stripScriptsAndTags(path);
        else if(path.content && isString(path.content))
            value = stripScriptsAndTags(path.content);
        if(matchesRegex && !matchesRegex.test(value))
            value = '';

        // If multiple objects:
        if(isArray(path)) {
            value = '';
            for(var i in path) {
                var pathEle = path[i];
                var str = null;
                if(isString(pathEle))
                    str = pathEle.strip();
                else if(pathEle.content && isString(pathEle.content))
                    str = pathEle.content.strip();
                if(str != null && str.length > 0 && (!matchesRegex || matchesRegex.test(str))) {
                    if(maxElms && maxElms == 1)
                        value = stripScriptsAndTags(str);
                    else
                        value += '<div style="margin-bottom: 4px;">'+stripScriptsAndTags(str)+' </div>';

                    if(maxElms && i+1 >= maxElms)
                        break;
                }
            }
        }
        //log("getContent() returing: '" + value + "'");
        return value;
    } catch (e) {
        log("getContent() error: " + e);
        return '';
    }
}

// Get standard stored content from a record, or null if none:
function getStoredContent(recordJson,fieldName){
    var value = null;
    try {
        if(recordJson.storedContent){
            var c = recordJson.storedContent.content;
            if(isArray(c)) {
                for(var i = 0; i < 	c.length; i++)
                    if(c[i].fieldName == fieldName)
                        value = c[i].content;
            }
            else if(c.fieldName == fieldName)
                value = c.content;
        }
    } catch (e) {}
    return value;
}

function renderJsonTree(i) {
    if(json != null) {
        var container = $('#jsonContainer'+i);
        var opener = $('#jsonOpener'+i);
        if(isEmpty(container)) {
            var recordJson = null;
            if(i == 0 && !isArray(json.DDSWebService.Search.results.record))
                recordJson = json.DDSWebService.Search.results.record;
            else
                recordJson = json.DDSWebService.Search.results.record[i];
        }
        $(container).toggle( 30, function() {
            if(!$(this).is(":visible")){
                $('#openerTxt'+i).html('View full record');
                $('#openerLnk'+i).html('+');
            }
            else {
                $('#openerTxt'+i).html('Close full record');
                $('#openerLnk'+i).html('-');
            }
        });
    }
}

// Perform the service request, fetching data into the given callback function:
function fetchJson(myUrl) {
    //log("JSONP request URL: " + myUrl);
    $.ajax({
        // myUrl aleady has all the parameters/data applied
        url: myUrl,
        // The name of the callback parameter, as specified by the YQL service
        jsonp: "callback",
        // Tell jQuery we're expecting JSONP
        dataType: "jsonp",
        // If timeout is reached, error condition is called:
        timeout: ajaxTimeout,
        // Work with the response - Note: for jsonp all data is sent to the callback function.
        success: function( jsonData ) {
            renderResponse(jsonData);
        },
        error: function (request, textStatus, errorThrown) {
            log("Ajax JSONP error, timeout reached: " + errorThrown);
            $('#searchResults').html('<p class="noMatches">The search server is taking longer than expected to respond. ' +
                'Please check your network connection and/or try again later.</p>');
            $('#loadMsg').hide();
        }
    });
}

// Add commas to a number for dislay
function formatNumber(nStr)
{
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1))
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    return x1 + x2;
}


function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


// Log to Firebug console:
function log(val) {
    if(window.console)
        window.console.log(val);
}


// -------------------- Begin Porter Stemmer ----------------

// Porter stemmer in Javascript. Few comments, but it's easy to follow against the rules in the original
// paper, in
//
//  Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14,
//  no. 3, pp 130-137,
//
// see also http://www.tartarus.org/~martin/PorterStemmer

// Release 1 be 'andargor', Jul 2004
// Release 2 (substantially revised) by Christopher McKenzie, Aug 2009

var stemmer = (function(){
    var step2list = {
            "ational" : "ate",
            "tional" : "tion",
            "enci" : "ence",
            "anci" : "ance",
            "izer" : "ize",
            "bli" : "ble",
            "alli" : "al",
            "entli" : "ent",
            "eli" : "e",
            "ousli" : "ous",
            "ization" : "ize",
            "ation" : "ate",
            "ator" : "ate",
            "alism" : "al",
            "iveness" : "ive",
            "fulness" : "ful",
            "ousness" : "ous",
            "aliti" : "al",
            "iviti" : "ive",
            "biliti" : "ble",
            "logi" : "log"
        },

        step3list = {
            "icate" : "ic",
            "ative" : "",
            "alize" : "al",
            "iciti" : "ic",
            "ical" : "ic",
            "ful" : "",
            "ness" : ""
        },

        c = "[^aeiou]",          // consonant
        v = "[aeiouy]",          // vowel
        C = c + "[^aeiouy]*",    // consonant sequence
        V = v + "[aeiou]*",      // vowel sequence

        mgr0 = "^(" + C + ")?" + V + C,               // [C]VC... is m>0
        meq1 = "^(" + C + ")?" + V + C + "(" + V + ")?$",  // [C]VC[V] is m=1
        mgr1 = "^(" + C + ")?" + V + C + V + C,       // [C]VCVC... is m>1
        s_v = "^(" + C + ")?" + v;                   // vowel in stem

    return function (w) {
        var 	stem,
            suffix,
            firstch,
            re,
            re2,
            re3,
            re4,
            origword = w;

        if (w.length < 3) { return w; }

        firstch = w.substr(0,1);
        if (firstch == "y") {
            w = firstch.toUpperCase() + w.substr(1);
        }

        // Step 1a
        re = /^(.+?)(ss|i)es$/;
        re2 = /^(.+?)([^s])s$/;

        if (re.test(w)) { w = w.replace(re,"$1$2"); }
        else if (re2.test(w)) {	w = w.replace(re2,"$1$2"); }

        // Step 1b
        re = /^(.+?)eed$/;
        re2 = /^(.+?)(ed|ing)$/;
        if (re.test(w)) {
            var fp = re.exec(w);
            re = new RegExp(mgr0);
            if (re.test(fp[1])) {
                re = /.$/;
                w = w.replace(re,"");
            }
        } else if (re2.test(w)) {
            var fp = re2.exec(w);
            stem = fp[1];
            re2 = new RegExp(s_v);
            if (re2.test(stem)) {
                w = stem;
                re2 = /(at|bl|iz)$/;
                re3 = new RegExp("([^aeiouylsz])\\1$");
                re4 = new RegExp("^" + C + v + "[^aeiouwxy]$");
                if (re2.test(w)) {	w = w + "e"; }
                else if (re3.test(w)) { re = /.$/; w = w.replace(re,""); }
                else if (re4.test(w)) { w = w + "e"; }
            }
        }

        // Step 1c
        re = /^(.+?)y$/;
        if (re.test(w)) {
            var fp = re.exec(w);
            stem = fp[1];
            re = new RegExp(s_v);
            if (re.test(stem)) { w = stem + "i"; }
        }

        // Step 2
        re = /^(.+?)(ational|tional|enci|anci|izer|bli|alli|entli|eli|ousli|ization|ation|ator|alism|iveness|fulness|ousness|aliti|iviti|biliti|logi)$/;
        if (re.test(w)) {
            var fp = re.exec(w);
            stem = fp[1];
            suffix = fp[2];
            re = new RegExp(mgr0);
            if (re.test(stem)) {
                w = stem + step2list[suffix];
            }
        }

        // Step 3
        re = /^(.+?)(icate|ative|alize|iciti|ical|ful|ness)$/;
        if (re.test(w)) {
            var fp = re.exec(w);
            stem = fp[1];
            suffix = fp[2];
            re = new RegExp(mgr0);
            if (re.test(stem)) {
                w = stem + step3list[suffix];
            }
        }

        // Step 4
        re = /^(.+?)(al|ance|ence|er|ic|able|ible|ant|ement|ment|ent|ou|ism|ate|iti|ous|ive|ize)$/;
        re2 = /^(.+?)(s|t)(ion)$/;
        if (re.test(w)) {
            var fp = re.exec(w);
            stem = fp[1];
            re = new RegExp(mgr1);
            if (re.test(stem)) {
                w = stem;
            }
        } else if (re2.test(w)) {
            var fp = re2.exec(w);
            stem = fp[1] + fp[2];
            re2 = new RegExp(mgr1);
            if (re2.test(stem)) {
                w = stem;
            }
        }

        // Step 5
        re = /^(.+?)e$/;
        if (re.test(w)) {
            var fp = re.exec(w);
            stem = fp[1];
            re = new RegExp(mgr1);
            re2 = new RegExp(meq1);
            re3 = new RegExp("^" + C + v + "[^aeiouwxy]$");
            if (re.test(stem) || (re2.test(stem) && !(re3.test(stem)))) {
                w = stem;
            }
        }

        re = /ll$/;
        re2 = new RegExp(mgr1);
        if (re.test(w) && re2.test(w)) {
            re = /.$/;
            w = w.replace(re,"");
        }

        // and turn initial Y back to y

        if (firstch == "y") {
            w = firstch.toLowerCase() + w.substr(1);
        }

        return w;
    }
})();

// -------------------- End Porter Stemmer ----------------
