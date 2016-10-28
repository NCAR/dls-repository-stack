var Pager = Class.create ({
	initialize: function (i, n) {
		this.mycase = -1;
		this.i = parseInt(i);
		this.n = parseInt(n);
		this.end_len = 2;
		this.span_len = 5;
		this.middle = this.getMiddle();
	},
	 
	showInputs: function (target) {
		var target = $(target);
		target.insert (new Element ("div").update ("cur: " + this.i));
		target.insert (new Element ("div").update ("num: " + this.n));
	},
	
	render: function (target) {
		var target = $(target);
		var array = $A();
		this.getPageList().each ( function (p) {
		if (p > 0) 
			array.push (p.toString());
		else
			array.push ("..");
		});
		target.update(array.join (" "));
	},
	 
	getMiddle: function () {
		var pad = Math.floor (this.span_len / 2);
		var left, right;
		if (this.i - pad < 1) {
			this.mycase = 1;
			left = 1;
			right = Math.min (this.span_len, this.n);
		}
		else if (this.i + pad > this.n) {
			this.mycase = 2;
			right = this.n;
			left = Math.max (1, this.n - this.span_len + 1);
		}
		else {
			this.mycase = 3;
			left = this.i - pad;
			right = this.i + pad;
		}
		return $R (left, right)
	},
	 
	getPageList: function () {
		var s = $A()
		if (this.n > this.end_len) {
			var left_end = $R (1, this.end_len);
			$A(left_end).each (function (i) {
				if (!this.middle.include(i))
					s.push (i);
			}.bind(this));
			
			if (left_end.end + 1 < this.middle.start) {
				s.push (-1);
			}
		}
		$A(this.middle).each ( function (i) {
			s.push (i);
		});
	 
		if (this.n > this.end_len) {
			var right_end = $R (this.n - this.end_len +1, this.n)
			if (this.middle.end + 1 < right_end.start) {
				s.push (-1)
			}
			$A(right_end).each ( function (i) {
				if (!this.middle.include(i))
					s.push (i)
			}.bind(this));
		}
		return s;
	}
});

var HtmlPager = Class.create (Pager, {
 
	render: function (target) {
		var target = $(target);
		target.update ( this.getPages (target) );
	},
	
	getPages: function (target) {
		var wrapper = new Element("span", {style:"whiteSpace:nowrap"});
		this.getPageList().each ( function (p) {
			if (p > 0) {
				if (p == this.i)
				wrapper.insert (this.getCurrentPageLink (p));
			else
				wrapper.insert (this.getPageLink (p));
			}
			else
				wrapper.insert (this.getSpacer());
		}.bind (this));
		return wrapper;
	},
		
	getSpacer: function () {
		return new Element ('li').update("...").addClassName ('pager-item');
	},
	
	getPageLink: function (page_num) {
		var href = this.getHref (page_num);
		var link = new Element ('a', {href:href}).update (page_num);
		link.addClassName ('pager-item');
		return link;
	},
	
	getHref: function (page_num) {
		var params = {p:page_num}
		return "baseUrl.jsp?" + $H(params).toQueryString();
	},
	
	getCurrentPageLink: function (page_num) {
		var pl = new Element ('li').update (page_num);
		pl.addClassName ("pager-item");
		pl.addClassName ('current-page');
		return pl;
	}
});

var DcsPager = Class.create (HtmlPager, {
	initialize: function ($super, i, n, nonPaigingParams, recsPerPage, contextPath) {
		// log ('DcsPager() nonPaigingParams: ' + nonPaigingParams);
		// this.recsPerPage = recsPerPage;
		this.nonPaigingParams = nonPaigingParams;
		this.recsPerPage = recsPerPage;
		this.contextPath = contextPath;
		$super(i, n);
		this.queryParams = this.nonPaigingParams.toQueryParams();
	},
	
	getHref: function (page_num) {
		this.queryParams.s = (page_num - 1) * this.recsPerPage;
		return this.contextPath+"/browse/query.do?" + $H(this.queryParams).toQueryString();
	},
	
	getPrevArrow: function (page_num) {
		var link = new Element ('a', {
			href : this.getHref(page_num - 1),
			title : "previous results page"
		});
		link.insert (new Element ('img', {
			src : this.contextPath + '/images/arrow_left.gif'
		}).addClassName ("pager-arrow"));
		return link;
	},
	
	getNextArrow: function (page_num) {
		var link = new Element ('a', {
			href : this.getHref(page_num + 1),
			title : "next results page"
		});
		link.insert (new Element ('img', {
			src : this.contextPath + '/images/arrow_right.gif'
		}).addClassName ("pager-arrow"));
		return link;
	},
	
	render: function (target) {
		target = $(target);
		if (!target) return;
		target.addClassName ("pager");
		target.update();
		// target.insert ("Results pages&nbsp;");
		if (this.i > 1)
			target.insert (this.getPrevArrow(this.i));
		target.insert (this.getPages());
		if (this.i < this.n)
			target.insert (this.getNextArrow(this.i));
	}
});
