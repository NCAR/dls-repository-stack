/* 
 This script implements a simple search page for a DDS or DCS repository.

 Requires:
 dds_search.html - The HTML portion of the implementaion
 prototype-1.6.x.js - Prototype JavaScript framework. See API docs at: http://www.prototypejs.org/

 Digital Learning Sciences (DLS)
 University Corporation for Atmospheric Research (UCAR)
 P.O. Box 3000
 Boulder, Colorado 80307 USA
 http://dlsciences.org/

 e-mail questions to: support@dlese.org

 This code may be copied and modified, free and with no warranty.
 */

var json = null;
var numResults = 10;
var parms = window.location.search.parseQuery();

if (!baseUrl)
    var baseUrl = '@DDS_SERVER_BASE_URL@/services/ddsws1-1';

Event.observe(window, 'load', onPageLoad);

var smartLinkQueries = new Hash();
var smartLinkBooserTerms = new Hash();
var smartLinkLabels = new Hash();
var smartLinkNum = 0;

var DDSSearch = {
    // Define a smart link, mapped to an http parameter:
    defineSmartLink: function (sl_httpParm, sl_label, sl_query, sl_boost_terms) {
        if (sl_httpParm && sl_label && sl_query) {
            smartLinkQueries.set(sl_httpParm, sl_query);
            smartLinkBooserTerms.set(sl_httpParm, sl_boost_terms);
            smartLinkLabels.set(sl_httpParm, sl_label);
        }
    }
}

function onPageLoad() {

    // Load smart links that are defined on <a> elements:
    $$('a.smartLink').each(function (elm) {
        var smParam = 'sl' + (++smartLinkNum);
        var label = elm.innerHTML;
        if (elm.getAttribute('altLabel') != null)
            label = elm.getAttribute('altLabel');
        if (label.length == 0)
            label = ' ';
        var query = elm.getAttribute('query');
        var boostTerms = elm.getAttribute('boost')
        if (boostTerms)
            boostTerms = boostTerms.split(",");
        log('define smartLink: httpParam:' + smParam + ' label:' + label + ' query:' + query + ' boostTerms:' + boostTerms);
        elm.setAttribute('href', '?sl=' + smParam);
        DDSSearch.defineSmartLink(smParam, label, query, boostTerms);
    });

    // Check if this is a Search action:
    if(typeof(parms.action) == 'undefined' && (typeof(parms.q) != 'undefined' || typeof(parms.sl) != 'undefined'))
        parms.action = 'Search';

    if (typeof(parms.action) != 'undefined') {
        doServiceRequest(parms.action);
    }

    // Set browser focus to the search box:
    if ($('searchForm'))
        $('searchForm').getInputs('text', 'q')[0].focus();

    if (typeof(showDeveloperInfo) != 'undefined' && showDeveloperInfo && $('showDeveloperInfo'))
        $('showDeveloperInfo').show();
}

function doServiceRequest(action) {
    if (typeof(action) != 'string')
        return;

    var request = null;

    if (action == 'Search') {
        var userQuery = getUserQuery();
        var s = parms.s;
        if (!s) s = 0;

        if (parms.sl && typeof(smartLinkQueries) != 'undefined') {
            var smartLinkQuery = '(' + smartLinkQueries.get(parms.sl) + ')';
            var boosterTerms = smartLinkBooserTerms.get(parms.sl);
            if (boosterTerms) {
                smartLinkQuery += ' AND ' + makeBooserQueryClause(boosterTerms);
            }
        }

        userQuery = userQuery.gsub('+', ' ').gsub('?', ' ').strip();
        if (userQuery.length == 0 && !smartLinkQuery) {
            $('searchResults').update('<p>You did not define a search. Please enter one or more keywords in the text box above. </p>');
            return;
        }

        if (userQuery.length > 0 && typeof(expandAndBoostSearch) != 'undefined' && expandAndBoostSearch)
            userQuery = '((' + userQuery + ') OR title:(' + userQuery + ') OR stems:(' + userQuery + ') OR titlestems:(' + userQuery + '))';

        var query = '(' + userQuery + ')';

        if (smartLinkQuery) {
            if (userQuery.length > 0)
                query = '(' + userQuery + ') AND (' + smartLinkQuery + ')';
            else
                query = '(' + smartLinkQuery + ')';
        }

        if (typeof(collections) != 'undefined')
            query += ' AND ky:(' + makeBooleanClause(collections, 'OR') + ')';

        if (typeof(xmlFormats) != 'undefined')
            query += ' AND xmlFormat:(' + makeBooleanClause(xmlFormats, 'OR') + ')';

        if (typeof(collections) != 'undefined')
            query += ' AND ky:(' + makeBooleanClause(collections, 'OR') + ')';

        if (typeof(virtualCollectionFilter) != 'undefined')
            query += ' AND (' + virtualCollectionFilter + ')';

        if (typeof(omitCollectionRecords) != 'undefined' && omitCollectionRecords == true)
            query += ' AND !xmlFormat:dlese_collect';

        var sort = '';
        if (typeof(sortAscendingBy) != 'undefined' && typeof(sortDescendingBy) != 'undefined')
            log('Client code error: You may specify sortAscendingBy -or- sortDescendingBy but not both. Ignoring...');
        else if (typeof(sortAscendingBy) != 'undefined')
            sort = '&sortAscendingBy=' + encodeURIComponent(sortAscendingBy);
        else if (typeof(sortDescendingBy) != 'undefined')
            sort = '&sortDescendingBy=' + encodeURIComponent(sortDescendingBy);

        // Request stored content for standard fields:
        var storedContentArgs = '&storedContent=title&storedContent=description&storedContent=url';

        log('query:' + query);

        // Construct the request to the service:
        request = baseUrl + '?verb=Search&client=js_client&output=json' + sort + '&transform=localize&callback=serviceJsonCallback&s=' + s + '&n=' + numResults + storedContentArgs + '&q=' + encodeURIComponent(query);
    }
    else if (action.startsWith('List') || action == 'ServiceInfo') {

        // Construct the request to the service:
        request = baseUrl + '?verb=' + action + '&client=js_client&output=json&transform=localize&callback=serviceJsonCallback';

        if (action == 'ListTerms' && parms.field)
            request += '&field=' + encodeURIComponent(parms.field);
    }

    if (request == null)
        return;

    if (typeof(showDeveloperInfo) != 'undefined' && showDeveloperInfo) {
        if ($('devRequestUrl'))
            $('devRequestUrl').show().href = request;
    }

    $('loadMsg').show();

    // Animate the loading msg:
    new PeriodicalExecuter(function (pe) {
        if ($('loadMsg').innerHTML.startsWith('Loading'))
            $('loadMsg').update($('loadMsg').innerHTML + '.');
        else
            pe.stop();
    }, 0.5);

    // If no response within 10 seconds, display message:
    new PeriodicalExecuter(function (pe) {
        if ($('loadMsg').innerHTML.startsWith('Loading'))
            $('loadMsg').update('Your request is taking longer than expected to respond. The server may be down or experiencing heavy traffic. Please try again later.');
        pe.stop();
    }, 10);

    // Get the search results as JSON, which calls serviceJsonCallback() on return
    fetchJson(request);
}

// This function boosts ranking of results that match the given terms or phrases without changing the result set itself - AND this with other clauses:
function makeBooserQueryClause(bt) {
    return '(allrecords:true OR title:(' + makeBooleanClause(bt, "OR") + ')^3 OR titlestems:(' + makeBooleanClause(bt, "OR") + ')^3 OR description:(' + makeBooleanClause(bt, "OR") + ')^3)';
}

// Service callback:
function serviceJsonCallback(json) {
    this.json = json;
    try {
        if (json != null && json.DDSWebService) {

            // Handle case of errors, zero results:
            if (json.DDSWebService.error) {
                theHTML = '';
                var code = json.DDSWebService.error.code;
                if (code == 'noRecordsMatch') {
                    theHTML = 'There were no matches';
                    if (parms.q)
                        theHTML += ' for <span class="queryText">' + parms.q.gsub('+', ' ') + '</span>';
                }
                else if (code == 'badQuery')
                    theHTML = 'There was a problem with the query syntax: ' + json.DDSWebService.error.content;
                else if (parms.action == 'Search')
                    theHTML = 'The were no matches for your search.';
                else
                    theHTML = 'No information is available. <!-- Error: The service may be down or the request may be invalid. -->';

                $('searchResults').update('<p>' + theHTML + '</p>');
                $('loadMsg').hide();
                return;
            }
            // Handle display of Search results, if one or more records:
            else if (json.DDSWebService.Search && json.DDSWebService.Search.results && json.DDSWebService.Search.results.record) {
                var searchHTML = '';
                var offset = Number(json.DDSWebService.Search.resultInfo.offset);
                var numReturned = Number(json.DDSWebService.Search.resultInfo.numReturned);
                var totalNumResults = Number(json.DDSWebService.Search.resultInfo.totalNumResults);

                // Create a pager
                if (parms.sl && typeof(smartLinkLabels) != 'undefined')
                    var smartLinkLabel = smartLinkLabels.get(parms.sl);

                var smartLinkLabelDisp = (smartLinkLabel ? ('<div class="smartLinkLabel">' + smartLinkLabel + '</div>') : '');
                var pager = '';
                var qArgs = '';
                if (parms.q)
                    qArgs += '&q=' + encodeURIComponent(parms.q);
                if (parms.sl)
                    qArgs += '&sl=' + encodeURIComponent(parms.sl);
                if (offset > 0)
                    pager += '<a href="?' + qArgs + '&s=' + (offset - numResults) + '">&#171; Prev</a> - ';
                if (parms.q)
                    pager += 'Results ';
                pager += (offset + 1) + ' - ' + (offset + numReturned) + ' out of ' + formatNumber(totalNumResults);
                if (offset + numResults < totalNumResults)
                    pager += ' - <a href="?' + qArgs + '&s=' + (offset + numResults) + '">Next &#187;</a>';
                pager = '<div class="pager">' + pager + '</div>';

                searchHTML += smartLinkLabelDisp + pager;

                var i = 0;
                // Handle multiple results:
                if (Object.isArray(json.DDSWebService.Search.results.record)) {
                    json.DDSWebService.Search.results.record.each(function (record) {
                        searchHTML = searchHTML + getRecordDisplayHtml(record, i++);
                    });
                }
                // Handle single result:
                else {
                    searchHTML = searchHTML + getRecordDisplayHtml(json.DDSWebService.Search.results.record, i++);
                }

                searchHTML += pager;
                $('searchResults').update('<p>' + searchHTML + '</p>');
                $('loadMsg').hide();
                return;
            }
            // Handle generic display of service responses (ListCollections, ListXmlFormats, ListFields, ServiceInfo):
            else if (json.DDSWebService) {
                $J('serviceResponse').render(json.DDSWebService);
                $('serviceResponse').show();
                $('loadMsg').hide();
                return;
            }
        }
    } catch (e) {
        var html = 'This browser appears not to support this operation. Message: ' + e;
        $('searchResults').update('<p>' + html + '</p>');
    }
    $('loadMsg').hide();
}

/* --
 Make a Lucene boolean search clause
 Args:
 arrayVar - an array object
 operator - one of 'AND' or 'OR'
 -- */
function makeBooleanClause(arrayVar, operator) {
    if (typeof(arrayVar) == 'string')
        return arrayVar;
    var clause = '';
    for (var i = 0; i < arrayVar.length; i++)
        clause += arrayVar[i] + (i == arrayVar.length - 1 ? '' : (' ' + operator + ' '));
    return clause;
}

// Hack to decode/encode UTF-8 characters (helps clean-up some data):
function decodeUtf8(s) {
    try {
        if (decodeURIComponent)
            return decodeURIComponent(escape(s));
        else
            return s;
    } catch (e) {
        return s;
    }
}

function encodeUtf8(s) {
    try {
        return unescape(encodeURIComponent(s));
    } catch (e) {
        return s;
    }
}

function getRecordDisplayHtml(recordJson, i) {
    var html = '';
    try {
        var title = decodeUtf8(getRecordTitle(recordJson));
        var description = decodeUtf8(getRecordDescription(recordJson));
        var url = getRecordUrl(recordJson);
        var collectionDesc = recordJson.head.collection.content.stripScripts().stripTags();
        var recId = recordJson.head.id.stripScripts().stripTags();

        if (!title.empty()) {
            title = title;
            title = hlKeywords(title);
            if (url.empty())
                html += '<div class="recordTitle">' + title + '</div>';
            else
                html += '<div class="recordTitle"><a href="' + url + '" target="_blank">' + title + '</a></div>';
        }
        if (title.empty() && !url.empty())
            html += '<div class="recordTitle"><a href="' + url + '" target="_blank">' + hlKeywords(url) + '</a></div>';
        if (!url.empty())
            html += '<div class="recordUrl">' + hlKeywords(url) + '</div>';
        if (!description.empty()) {
            description = description;
            html += '<div class="recordDesc">' + hlKeywords(description) + '</div>';
        }
        if (!html.empty()) {
            html += '<div class="subContent recordSource">From: ' + collectionDesc + '</div>';
            if (typeof(displayId) != 'undefined' && displayId)
                html += '<div class="subContent recordSource">ID: ' + recId + '</div>';
        }
    } catch (e) {
        alert("Error extracting data: " + e);
    }
    if (html.empty()) {
        html += '<div class="recordTitle">Matching record: ID ' + recordJson.head.id + '</div>';
        html += '<div class="subContent recordSource">From: ' + recordJson.head.collection.content + '</div>';
    }
    if (typeof(displayFullRecordMetadata) != 'undefined' && displayFullRecordMetadata) {
        html += '<div class="jsonOpener"><span id="jsonOpener' + i + '" onclick="renderJsonTree(' + i + ')"><a href="javascript:void(0)" class="openerLnk">+</a> <a href="javascript:void(0)" class="openerTxt">View full record</a></span></div>';
        html += '<div class="jsonContainer" id="jsonContainer' + i + '" style="display:none"></div>';
    }
    return '<div class="record">' + html + '</div>';
}

function hlKeywords(str) {
    if (!str || (typeof(highlightKeywords) != 'undefined' && !highlightKeywords)) return str;
    var userQuery = getUserQuery();
    userQuery.split(/\W+/).each(function (term) {
        if (!isStopWord(term) && term.strip().length > 0) {
            var stem = stemmer(term);
            var pattern = new RegExp('((\\W|^)(' + stem + '\\w*)(\\W|$))|((\\W|^)(' + term + '\\w*)(\\W|$))', 'gi');
            str = str.replace(pattern, ('$2$6<em>$3$7</em>$4$8'));
        }
    });
    return str;
}

function isStopWord(term) {
    return ENGLISH_STOP_WORDS.any(function (n) {
        return term.toLowerCase() == n
    });
}

var ENGLISH_STOP_WORDS = [
    "a", "an", "and", "are", "as", "at", "be", "but", "by",
    "for", "if", "in", "into", "is", "it",
    "no", "not", "of", "on", "or", "s", "such",
    "t", "that", "the", "their", "then", "there", "these",
    "they", "this", "to", "was", "will", "with", "em"
];

function getUserQuery() {
    var userQuery = parms.q;

    if (userQuery)
        $('searchForm').getInputs('text', 'q')[0].value = parms.q.gsub('+', ' ');
    else if ($('searchForm'))
        userQuery = $('searchForm').getInputs('text', 'q')[0].value.strip();
    else
        userQuery = '';
    return userQuery;
}

function getRecordTitle(recordJson) {
    try {
        // Standard stored content for title:
        var value = getStoredContent(recordJson, 'title');
        if (value == null) {
            // ADN record:
            if (recordJson.metadata.itemRecord)
                value = getContent(recordJson.metadata.itemRecord.general.title, 1);
            // NCAR library_dc record:
            else if (recordJson.head.xmlFormat == 'library_dc')
                value = getContent(recordJson.metadata.record.title, 1);
            // ncs_item, MSP2 record:
            else if (recordJson.metadata.record)
                value = getContent(recordJson.metadata.record.general.title, 1);
            // DDS/DLESE Collection record:
            else if (recordJson.metadata.collectionRecord) {
                value = recordJson.metadata.collectionRecord.general.fullTitle;
                if (!value)
                    value = recordJson.metadata.collectionRecord.general.shortTitle;
            }
            // DLESE annotation record:
            else if (recordJson.metadata.annotationRecord)
                value = getContent(recordJson.metadata.annotationRecord.annotation.title, 1);
            // DLESE news opps record:
            else if (recordJson.metadata['news-oppsRecord'])
                value = getContent(recordJson.metadata['news-oppsRecord'].title, 1);
            // Concept record:
            else if (recordJson.metadata.concept)
                value = getContent(recordJson.metadata.concept.shortTitle, 1);
            // OAI DC record:
            else if (recordJson.metadata.dc)
                value = getContent(recordJson.metadata.dc.title, 1);
            // NSDL DC record:
            else if (recordJson.metadata.nsdl_dc)
                value = getContent(recordJson.metadata.nsdl_dc.title, 1);
            // NSDL NCS Collection record:
            else if (recordJson.head.xmlFormat == 'ncs_collect')
                value = getContent(recordJson.metadata.record.general.title, 1);
            // MODS record:
            else if (recordJson.metadata.mods) {
                value = getContent(recordJson.metadata.mods.titleInfo.title, 1);
                if (value == '')
                    value = getContent(recordJson.metadata.mods.titleInfo[0].title, 1);
            }
        }
        return (Object.isString(value) ? value : '');
    } catch (e) {
        return '';
    }
}

function getRecordDescription(recordJson) {
    try {
        // Standard stored content for description:
        var value = getStoredContent(recordJson, 'description');
        if (value == null) {
            // ADN record:
            if (recordJson.metadata.itemRecord)
                value = getContent(recordJson.metadata.itemRecord.general.description);
            // NCAR library_dc record:
            else if (recordJson.head.xmlFormat == 'library_dc')
                value = getContent(recordJson.metadata.record.description);
            // ncs_item, MSP2 record:
            else if (recordJson.metadata.record)
                value = getContent(recordJson.metadata.record.general.description);
            // DLESE Collection record:
            else if (recordJson.metadata.collectionRecord)
                value = getContent(recordJson.metadata.collectionRecord.general.description);
            // DLESE annotation record:
            else if (recordJson.metadata.annotationRecord)
                value = getContent(recordJson.metadata.annotationRecord.annotation.content.description);
            // DLESE news opps record:
            else if (recordJson.metadata['news-oppsRecord'])
                value = getContent(recordJson.metadata['news-oppsRecord'].description);
            // Concept record:
            else if (recordJson.metadata.concept)
                value = getContent(recordJson.metadata.concept.longTitle);
            // OAI DC record:
            else if (recordJson.metadata.dc)
                value = getContent(recordJson.metadata.dc.description);
            // NSDL DC record:
            else if (recordJson.metadata.nsdl_dc)
                value = getContent(recordJson.metadata.nsdl_dc.description);
            // NSDL NCS Collection record:
            else if (recordJson.head.xmlFormat == 'ncs_collect')
                value = getContent(recordJson.metadata.record.general.description);
            // MODS record:
            else if (recordJson.metadata.mods) {
                value = getContent(recordJson.metadata.mods.note);
            }
        }
        return (Object.isString(value) ? value : '');
    } catch (e) {
        return '';
    }
}

function getRecordUrl(recordJson) {
    try {
        // Standard stored content for url:
        var value = getStoredContent(recordJson, 'url');
        if (value == null) {
            var urlRegEx = /http(.)*|ftp(.)*/;

            // ADN record:
            if (recordJson.metadata.itemRecord)
                value = recordJson.metadata.itemRecord.technical.online.primaryURL;
            // NCAR library_dc record:
            else if (recordJson.head.xmlFormat == 'library_dc')
                value = getContent(recordJson.metadata.record.URL, 1, urlRegEx);
            // ncs_item, MSP2 record:
            else if (recordJson.head.xmlFormat == 'msp2' || recordJson.head.xmlFormat == 'msp')
                value = getContent(recordJson.metadata.record.general.url, 1, urlRegEx);
            // DLESE Collection record:
            else if (recordJson.metadata.collectionRecord)
                value = recordJson.metadata.collectionRecord.access.collectionLocation;
            // DLESE annotation record:
            else if (recordJson.metadata.annotationRecord)
                value = recordJson.metadata.annotationRecord.annotation.content.url;
            // DLESE news opps record:
            else if (recordJson.metadata['news-oppsRecord'])
                value = recordJson.metadata['news-oppsRecord'].announcementURL;
            // Concept record:
            else if (recordJson.metadata.concept)
                value = null;
            // OAI DC record:
            else if (recordJson.metadata.dc)
                value = getContent(recordJson.metadata.dc.identifier, 1, urlRegEx);
            // NSDL DC record:
            else if (recordJson.metadata.nsdl_dc)
                value = getContent(recordJson.metadata.nsdl_dc.identifier, 1, urlRegEx);
            // NSDL NCS Collection record:
            else if (recordJson.head.xmlFormat == 'ncs_collect')
                value = getContent(recordJson.metadata.record.general.url, 1, urlRegEx);
            // OSM 1.1 (next):
            else if (recordJson.head.xmlFormat == 'osm' || recordJson.head.xmlFormat == 'osm_next')
                value = getContent(recordJson.metadata.record.resources.primaryAsset.url, 1, urlRegEx);
            // MODS record:
            else if (recordJson.metadata.mods) {
                value = getContent(recordJson.metadata.mods.location.url, 1, urlRegEx);
                if (value == '')
                    value = getContent(recordJson.metadata.mods.identifier, 1, urlRegEx);
            }
        }
        return (Object.isString(value) ? value : '');
    } catch (e) {
        log("getRecordUrl() error: " + e);
        return '';
    }
}

var testMe = 'fztp://myurl.com';
//var result = test.match(/http(.)*/);
//alert('test match:' + (/http(.)*|ftp(.)*/.test(testMe)));

// Handle json paths for repeating fields, fields with content (e.g. xml attributes), dc variations:
function getContent(path, maxElms, matchesRegex) {
    try {
        var value = '';

        // If a single object:
        if (!path)
            value = '';
        else if (Object.isString(path))
            value = path.stripScripts().stripTags();
        else if (path.content && Object.isString(path.content))
            value = path.content.stripScripts().stripTags();
        if (matchesRegex && !matchesRegex.test(value))
            value = '';

        // If multiple objects:
        if (Object.isArray(path)) {
            value = '';
            for (var i in path) {
                var pathEle = path[i];
                var str = null;
                if (Object.isString(pathEle))
                    str = pathEle.strip();
                else if (pathEle.content && Object.isString(pathEle.content))
                    str = pathEle.content.strip();
                if (str != null && str.length > 0 && (!matchesRegex || matchesRegex.test(str))) {
                    if (maxElms && maxElms == 1)
                        value = str.stripScripts().stripTags();
                    else
                        value += '<div style="margin-bottom: 4px;">' + str.stripScripts().stripTags() + ' </div>';

                    if (maxElms && i + 1 >= maxElms)
                        break;
                }
            }
        }
        log("getContent() returing: '" + value + "'");
        return value;
    } catch (e) {
        log("getContent() error: " + e);
        return '';
    }
}

// Get standard stored content from a record, or null if none:
function getStoredContent(recordJson, fieldName) {
    var value = null;
    try {
        if (recordJson.storedContent) {
            var c = recordJson.storedContent.content;
            if (Object.isArray(c)) {
                for (var i = 0; i < c.length; i++)
                    if (c[i].fieldName == fieldName)
                        value = c[i].content;
            }
            else if (c.fieldName == fieldName)
                value = c.content;
        }
    } catch (e) {
    }
    return value;
}

function renderJsonTree(i) {
    if (json != null) {
        var container = $('jsonContainer' + i);
        var opener = $('jsonOpener' + i);
        if (container.empty()) {
            var recordJson = null;
            if (i == 0 && !Object.isArray(json.DDSWebService.Search.results.record))
                recordJson = json.DDSWebService.Search.results.record;
            else
                recordJson = json.DDSWebService.Search.results.record[i];
            $J(container).render(recordJson.metadata);
        }
        if ($(container).visible()) {
            $(opener).select('.openerTxt')[0].update('View full record');
            $(opener).select('.openerLnk')[0].update('+');
            $(container).hide();
        }
        else {
            $(opener).select('.openerTxt')[0].update('Close full record');
            $(opener).select('.openerLnk')[0].update('-');
            $(container).show();
        }
    }
}


// Perform the service request, fetching data into the given callback function:
function fetchJson(myUrl) {
    if ($('jsonScriptElm'))
        $('jsonScriptElm').remove();

    var scriptElm = new Element("script", {src: myUrl, id: 'jsonScriptElm'});

    // Insert the script in the document head, which executes the given callback function
    document.getElementsByTagName('head')[0].appendChild(scriptElm);
}

// Add commas to a number for dislay
function formatNumber(nStr) {
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1))
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    return x1 + x2;
}


// Log to Firebug console:
function log(val) {
    if (window.console)
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

var stemmer = (function () {
    var step2list = {
            "ational": "ate",
            "tional": "tion",
            "enci": "ence",
            "anci": "ance",
            "izer": "ize",
            "bli": "ble",
            "alli": "al",
            "entli": "ent",
            "eli": "e",
            "ousli": "ous",
            "ization": "ize",
            "ation": "ate",
            "ator": "ate",
            "alism": "al",
            "iveness": "ive",
            "fulness": "ful",
            "ousness": "ous",
            "aliti": "al",
            "iviti": "ive",
            "biliti": "ble",
            "logi": "log"
        },

        step3list = {
            "icate": "ic",
            "ative": "",
            "alize": "al",
            "iciti": "ic",
            "ical": "ic",
            "ful": "",
            "ness": ""
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
        var stem,
            suffix,
            firstch,
            re,
            re2,
            re3,
            re4,
            origword = w;

        if (w.length < 3) {
            return w;
        }

        firstch = w.substr(0, 1);
        if (firstch == "y") {
            w = firstch.toUpperCase() + w.substr(1);
        }

        // Step 1a
        re = /^(.+?)(ss|i)es$/;
        re2 = /^(.+?)([^s])s$/;

        if (re.test(w)) {
            w = w.replace(re, "$1$2");
        }
        else if (re2.test(w)) {
            w = w.replace(re2, "$1$2");
        }

        // Step 1b
        re = /^(.+?)eed$/;
        re2 = /^(.+?)(ed|ing)$/;
        if (re.test(w)) {
            var fp = re.exec(w);
            re = new RegExp(mgr0);
            if (re.test(fp[1])) {
                re = /.$/;
                w = w.replace(re, "");
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
                if (re2.test(w)) {
                    w = w + "e";
                }
                else if (re3.test(w)) {
                    re = /.$/;
                    w = w.replace(re, "");
                }
                else if (re4.test(w)) {
                    w = w + "e";
                }
            }
        }

        // Step 1c
        re = /^(.+?)y$/;
        if (re.test(w)) {
            var fp = re.exec(w);
            stem = fp[1];
            re = new RegExp(s_v);
            if (re.test(stem)) {
                w = stem + "i";
            }
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
            w = w.replace(re, "");
        }

        // and turn initial Y back to y

        if (firstch == "y") {
            w = firstch.toLowerCase() + w.substr(1);
        }

        return w;
    }
})();

// -------------------- End Porter Stemmer ----------------


/* -------------------- Begin JsonViewerElement - This code section only needed if displayFullRecordMetadata or showDeveloperInfo is set to true --------------------
 Displays json structures.
 extends prototypes "Element" class.

 Use:

 var JSONObject = { ... object ... }
 $J('some-target-element').render(JSONObject);

 Required library: Prototype JavaScript framework (v1.6.0.2): http://www.prototypejs.org/
 */

// Element is not a Class, so we can't subclass it!
var JsonViewerElement = Class.create();
Object.extend(JsonViewerElement, Element);

JsonViewerElement.addMethods({
    addElement: function (element, tag) {
        element = $J(element);
        return $J(element.appendChild(new Element(tag)));
    },

    addDiv: function (element) {
        return $J(element).addElement("div");
    },
    // key is the json object's name
    setKey: function (element, key) {
        element = $J(element)
        kSpan = element.addElement("span");
        kSpan.update(key);
        kSpan.addClassName("key");
        return element;
    },

    /*  value is assocated with key */
    setValue: function (element, key, value) {
        var val = element.addElement("span");

        // if value is already a link, simply insert it
        if (Object.isElement(value)) {
            val.insert(value);
        }
        // Create a link if appropriate:
        else {
            if (Object.isString(value) && Object.isString(key) && key == 'field') {
                var h = '?action=ListTerms&field=' + encodeURIComponent(value);
                var link = new Element("a", {href: h}).update(value);
                link.addClassName("jsonLnk");
                val.insert(link);
            }
            else if (Object.isString(value) && (value.startsWith('http://') || value.startsWith('https://')) && value.search('\\s+') == -1) {
                var link = new Element("a", {href: value, target: '_blank'}).update(value);
                link.addClassName("jsonLnk");
                val.insert(link);
            }
            else
                val.insert(value);
        }
        return val;
    },

    addJobj: function (element, key) {
        var obj = $J(element).addDiv();
        obj.addClassName("json-object");
        obj.setKey(key);
        return obj;
    },

    addJheader: function (element, key) {
        var obj = $J(element).addDiv();
        obj.addClassName("json-header");
        obj.setKey(key);
        return obj;
    },

    // renders a key = value json element
    addJvalue: function (element, key, value) {
        // handle multi-values

        if (typeof (value) == "string" || Object.isElement(value)) {
            var obj = $J(element).addDiv();
            obj.addClassName("json-value");
            obj.setKey(key);
            obj.insert("&nbsp;=&nbsp;");
            obj.setValue(key, value);
            // obj.insert(new Element ("span").update (value));
            return obj;
        }

        if (typeof (value) == "object") {
            var obj = element.addJobj(key);
            return obj.render(value);
        }
    },

    /* renders "obj" (a jasonViewer element) as child of "element" */
    render: function (element, obj) {
        element = $J(element);
        $H(obj).each(function (pair) {
            var valType = typeof (pair.value);
            switch (valType) {
                case "undefined":
                case "function":
                case "unknown":
                    return;
                case "boolean":
                    element.addJvalue(pair.key, pair.value.toString());
                    return;
                case "string":
                    element.addJvalue(pair.key, pair.value);
                    break;
                case "object":
                    // if the value is an array, show each value with the same key
                    if (Object.isArray(pair.value)) {
                        pair.value.each(function (val) {
                            element.addJvalue(pair.key, val);
                        });
                    }
                    else {
                        var obj = element.addJobj(pair.key);
                        obj.render(pair.value);
                    }
                    return;

                default:
                    // log("unknown type: " + objType + " for key: " + pair.key);
                    return;
            }
        });
    }
});

/* alias for JsonViewerElement.extend */
function $J(element) {
    if (arguments.length > 1) {
        for (var i = 0, elements = [], length = arguments.length; i < length; i++)
            elements.push($J(arguments[i]));
        return elements;
    }
    if (typeof element == 'string')
        element = $(element);
    return JsonViewerElement.extend(element);
}

// -------------------- End JsonViewerElement --------------------


