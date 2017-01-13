/**
 * 基本操作函数V1.1
 *
 * User: HOTLEAVE
 * Date: 12-6-9
 * Time: 上午10:49
 */

/**
 * changelog
 * v1.1 调整createDelegate方法在IE8下不兼容的问题
 */

/**
 * 生成命令空间
 * @param 命名空间
 */
function namespace() {
    "use strict";
    var a = arguments, o = null, i, j, d, r;
    for (i = 0; i < a.length; i++) {
        d = a[i].split(".");
        r = d[0];

        // 相当于 if (typeof com == undefined) {com = {};} o = com;
        eval("if(typeof " + r + " == 'undefined'){window['" + r + "']={};} o=" + r + ";");

        for (j = 1; j < d.length; j++) {
            o[d[j]] = o[d[j]] || {};

            // 处理下一级
            o = o[d[j]];
        }
    }

    return o;
}

/**
 * 返回一个函数， 这个函数调用原函数，原函数中的this指向obj
 * @param obj
 * @param args
 * @param appendArgs
 *          falsy-调用function2时传的参数被忽略，args数组参数作为function1的参数运行
 *          true- 调用 function2时传的参数放在args数组前面合成一个新的数组，作为function1的参数运行
 *          number-假设调用function2时传的参数为array1,那么将args数组插入到array1的指定number位置, 然后再把最终数组作为function1的参数运行
 * @return {Function} function2
 */
Function.prototype.createDelegate = function (obj, args, appendArgs) {
    var method = this;
    return function () {
        var callArgs = args || arguments;
        if (appendArgs === true) {
            callArgs = Array.prototype.slice.call(arguments, 0);
            callArgs = callArgs.concat(args);
        } else if (typeof appendArgs == "number") {
            // copy arguments first
            callArgs = Array.prototype.slice.call(arguments, 0);
            // create method call params
            var applyArgs = [appendArgs, 0].concat(args);
            // splice them in
            Array.prototype.splice.apply(callArgs, applyArgs);
        }
        return method.apply(obj || window, callArgs);
    };
}

/**
 * javascript 中判断某一String是否以什么结尾
 * @param s
 * @returns {Boolean}
 */
String.prototype.endWith = function(s) {
	if (s == null || s == "" || this.length == 0 || s.length > this.length)
		return false;
	if (this.substring(this.length - s.length) == s)
		return true;
	else
		return false;
	return true;
}
/**
 * javascript 中判断某一String是否以什么开始
 * @param s
 * @returns {Boolean}
 */
String.prototype.startWith = function(s) {
	if (s == null || s == "" || this.length == 0 || s.length > this.length)
		return false;
	if (this.substr(0, s.length) == s)
		return true;
	else
		return false;
	return true;
}


/**
 * 格式化当前时间
 * @param format
 * @return
 */
Date.prototype.format = function(format) {
	try{
	    var o = {
	        "M+" :this.getMonth() + 1,
	        "d+" :this.getDate(),
	        "h+" :this.getHours(),
	        "m+" :this.getMinutes(),
	        "s+" :this.getSeconds(),
	        "q+" :Math.floor((this.getMonth() + 3) / 3),
	        "S" :this.getMilliseconds()
	    };
	    if (/(y+)/.test(format)) {
	        format = format.replace(RegExp.$1, (this.getFullYear() + "")
	                .substr(4 - RegExp.$1.length));
	    }
	    for ( var k in o) {
	        if (new RegExp("(" + k + ")").test(format)) {
	            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
	                    : ("00" + o[k]).substr(("" + o[k]).length));
	        }
	    }
	}catch(error){}
    return format;
    
};
	

/**
 * javascript 获取URL参数值
 */
function QueryString(fieldName){
	var urlString = document.location.search;
	if(urlString != null){
		var typeQu = fieldName+"=";
		var urlEnd = urlString.indexOf(typeQu);
		if(urlEnd != -1){
			 var paramsUrl = urlString.substring(urlEnd+typeQu.length);
			 var isEnd =  paramsUrl.indexOf('&');
			 if(isEnd != -1){
			 	return paramsUrl.substring(0, isEnd);
			 }else{
			 	return paramsUrl;
			 }
		}else{
			return null;
		}
	}else{
		return null;
	}
};

/**
 * 删除数组中的元素
 *
 * @param index 元素索引, 从0开始
 * @return {*}
 */
Array.prototype.del = function(index) {
    try{
    if (index < 0) {
        return this;
    }
    return this.slice(0, index).concat(this.slice(index + 1, this.length));
    }catch(error){
        return this;
    }
}

/**
 * 获取JS的参数
 *
 * @param filename JS文件名
 * @return {Object}
 */
function getParameter(filename) {
    var url, index, scripts = $('script'), params = {};
    $.each(scripts, function() {
        url = this.src;
        if (url.indexOf(filename) >= 0) {
            index = url.indexOf('?');
            if (index > 0) {
                url = url.substring(index + 1);
                url = (url ? url.split('&') : []);

                $.each(url, function() {
                    var temp = this.split('=');
                    params[temp[0]] = temp[1];
                });
            }

           // return false;
        }
    });

    return params;
}

function getPageBase(pageId, options) {
    var pkg = (pageId == '' ? 'com.cenmobile.base' : 'com.powerem.' + pageId);
    var pageBase = com.cenmobile.ns(pkg);

    pageBase = $.extend(true, pageBase, com.cenmobile.base);
    pageBase.pkg = pkg;

    options = options || {};
    options.pageId = pageId;
    pageBase.settings(options);

    return pageBase;
}

function log(obj) {
    if (window.console && window.console.log && window.console.dir) {
        if (typeof obj == 'object') {
            console.dir(obj);
        } else {
            console.log(obj);
        }
    }
}

namespace("com.cenmobile.base");
com.cenmobile['ns'] = namespace;

/**
 * 基本操作处理
 */
$.extend(true, com.cenmobile.base, {
    defaultConfig:{
        searchAreaId:'searcharea', // 查询区域ID
        actionNameSpace:'', // action名称, 需要指定
        pageId:'', // 页面标签符
        /**
         * 列表配置
         */
        jqGridConfig:{
            appendToolbar: true, // 添加新按钮时,是否删除原有按钮
            jqGridId:'tablegrid', // 列表ID
            pagerId:'pager', // 分页区域ID
            actionUrl:'list.action', // 列表数据的获取地址
            exportExcel: false,
            exportPDF: false,
            exportWord:false,
            autoSize: false,
            rowNum:10,
            caption:'列表',
            sortname:'created',
            sortorder:'DESC',
            mtype:"POST",
            datatype:"json",
            viewrecords:true,
            beforeDelete:function(obj){return true;},
            multiselect:true,
            autoHeader:false, // 表头默认不只适应
            rownumbers:true,  // 显示序号
            shrinkToFit:true,
            rowList:[10, 15, 20, 50, 100],
            width:'100%',
            height:'360px',
            autowidth:true, // 宽度自适应
            multiboxonly:true, // 选择checkbox才会起作用
            jsonReader: {
                root: 'rows',
                total: 'total',
                page: 'page',
                records: 'records',
                id: 'id',
                repeatitems: false
            },

            // 工具栏
            toolbars:{
                display:true, // 是否显示
                buttons:{
                    '增加':function () {
                    	if (this.config.dialogConfig.param && this.config.dialogConfig.param.id) {
                    		delete this.config.dialogConfig.param.id;
                    	}
                        this.showDialog();
                    },
                    '删除':function () {
                        this.deleteRecord();
                    }
                }
            }
        },

        /**
         * 对话框配置
         */
        dialogConfig:{
            dialogId:'dialogadd',
            url: 'about:blank',
            noBtn: false,
            lock: true,
            width: 600,
            height: 300,
            cancel: true,
            slide:false, // 是否滑动弹出，默认为false，不使用动画效果
            /**
             * 打开Dialog前调用的函数, 如果返回值不为true, 则停止显示
             *
             * @param id 当前业务逻辑ID
             * @param readonly 是否只读
             * @return {Boolean} true-继续显示, false-停止显示
             */
            beforeShowDialog: function(id, readonly) {
                return true;
            }
        },

        /**
         * 表单配置
         */
        formConfig:{
            formId:'addForm',
            actionUrl:'save.action', // 保存地址
            deleteUrl:'delete.action', // 删除地址
            type:'post',
            dataType:'json',
            contentType: "application/x-www-form-urlencoded; charset=utf-8", // form提交时的编码
            clearForm:true,
            resetForm:true,
            beforeSubmit:function (values, form, options) {
                $(form).find('div[type=file]').attachment('putValue2Form', $(form).attr('id'));
                var rst = $(form).validate().form();
                if (rst) {
                    form.data('submit', true);
                }

                return rst;
            },

            /**
             * 保存成功回调函数, this指向子页面pageBase
             *
             * @param win 父页面window对象
             */
            successCallback: function(win) {},
            // 对话框中内容加载配置(针对单实体, 列表请用jqGrid自行配置)
            dataLoader: {
                url: 'getByID.action', // 数据地址
                prefix: '', // 字段名称前缀
                param: {}, // 参数

                /**
                 * 回调函数
                 * @param data 从服务器加载的数据
                 * @param readonly 是否只读
                 * @param prefix 名字名称前缀
                 */
                loader: function(data, readonly, prefix) {
                    this.loadData2Form(this.config.formConfig.formId, data, readonly, prefix);
                }
            }
        }
    },

    /**
     * 初始化函数
     *
     * @param pageId 当前页面标识符
     * @param callback 初始化完成后调用
     */
    onloadFunction:function (options, callback) {
        var config = this.settings(options);
        this.bindSearchBtnEvent(config.searchAreaId, this.queryBtnHandler.createDelegate(this, [this.config.searchAreaId, this.config.jqGridConfig.jqGridId]));
        //搜索条件样式
        var searchAreaP = $('#'+ this.getIdByPageId(config.searchAreaId));
		searchAreaP.addClass("searcharea");
        this.initJqGrid(config.jqGridConfig.jqGridId, config.jqGridConfig);

        this.initForm(config.formConfig.formId);

        if (typeof callback == 'function') {
            callback.call(this);
        }
    },

    /**
     * 初始化
     */
    settings: function(options) {
        if (typeof options == 'string') {
            return this.plainObject(this.config)[options];
        } else {
            this.config = $.extend(true, {}, this.defaultConfig, this.config || {}, options || {});
            this.pageId = this.config.pageId || '';

            if (this.config.jqGridConfig.appendToolbar !== true) {
                this.config.jqGridConfig.toolbars.buttons = options.jqGridConfig.toolbars.buttons || {};
            }

            return this.config;
        }
    },

    /**
     * 绑定查询按钮点击事件
     * @param id searchAreaId, 默认为searcharea
     * @param clickHandler 点击事件处理函数
     */
    bindSearchBtnEvent:function (id, clickHandler) {
        var button = $('input[type=button]', '#' + this.getIdByPageId(id));
        button.attr('class', 'btnsearch_mouseout');
        button.bind('mouseover', function () {
            $(this).attr('class', 'btnsearch_mouseover');
        });
        
        button.bind('mouseout', function () {
            $(this).attr('class', 'btnsearch_mouseout');
        });
        button.bind('click', clickHandler);
        
        var searchArea = $("#searcharea");
        // 日期控件
        var dateInputs = $('input[customtype=date]', '#' + this.getIdByPageId(id));
        $.each(dateInputs, function(k, v) {
            $(v).attr('readonly', 'true');
            $(v).datepicker({
                dateFormat: $(v).attr('dateFormat') || 'yy-mm-dd',
                beforeShow: function (i, e) {
                    var z = jQuery(i).closest(".ui-dialog").css("z-index") + 4;
                    e.dpDiv.css('z-index', z);
                }
            });
        });
    },

    /**
     * 查询
     *
     * @param seachAreaId 查询区域Div的ID
     * @param jqGridTableId 列表ID
     */
    queryBtnHandler:function (searchAreaId, jqGridTableId) {
        var searchArea = $('#' + this.getIdByPageId(searchAreaId));
        var input = $('input,select', searchArea);
        var data = {};

        $.each(input, function (k, v) {
            if (v.type != 'button' && (typeof $(v).val() != 'undefined')) {
                data['search_' + v.name] = $(v).val();
                data[v.name + '_opt'] = $(v).attr('opt') || '=';
                data[v.name + '_logic'] = $(v).attr('logic') || ' and ';
            }
        });

        $('#' + this.getIdByPageId(jqGridTableId)).jqGrid('setGridParam', {
            postData:data,
            page:1
        }).trigger("reloadGrid");
    },

    /**
     * 初始化列表
     *
     * @param id 列表的tableId
     * @param options 列表配置
     */
    initJqGrid:function (id, options) {
        var newId = this.getIdByPageId(id);
        var self = this;
        var colModel = this._buildColumeModel(options.columns);
        options['colNames'] = colModel.colNames;
        options['colModel'] = colModel.colModel;

        var config = {
            pager:'#' + this.getIdByPageId(this.config.jqGridConfig.pagerId),
            toolbar:[this.config.jqGridConfig.toolbars.display, 'top']
            /*ondblClickRow:function (rowid, iRow, iCol, e) {
                self.dblClickHandler.call(self, rowid, iRow, iCol, e);
            }*/
        };

        config = $.extend(true, config, this.config.jqGridConfig, options || {});
        config.url = this.getFullPath(config.actionUrl);

        if (config.appendToolbar !== true) {
            config.toolbars.buttons = options.toolbars ? (options.toolbars.buttons || {}) : {};
        }


        if (config.exportExcel) {
            config.toolbars.buttons['导出Excel'] = function() {
                $('#' + newId).exportExcel(options);
            };
        }

        if (config.exportPDF) {
            config.toolbars.buttons['导出PDF'] = function () {
                $('#' + newId).exportPDF(options);
            };
        }
        
        if (config.exportWord) {
            config.toolbars.buttons['导出Word'] = function () {
                $('#' + newId).exportWord(options);
            };
        }


        if (colModel.groupedCols.length > 0) {
            config.gridComplete = function() {
                var toBeRemove = [];
                $.each(colModel.groupedCols, function() {
                    toBeRemove.push(self._jqGridRowMerger(newId, this, colModel.groupedCols));
                });

                $.each(toBeRemove, function() {
                    $.each(this, function(k, v) {
                        $(v).remove();
                    });
                });
            }
        }
        var grid = $('#' + newId);
        grid.jqGrid(config);
        if(config.autoHeader) {
            $(".ui-jqgrid .ui-th-div-ie").css({"white-space":"pre-wrap","zoom" :1,"height":"auto","*white-space":"pre", "*word-wrap":"break-word"});
            $(".ui-jqgrid .ui-th-div-ie").attr('style','white-space:pre-wrap;zoom:1;height:auto;*white-space:pre;*word-wrap:break-word');
            var width = config.width;
            var index = new String(config.width).indexOf("%");
            if(index  != -1) {
                var temp = new String(config.width).substr(0,index);
                width = document.documentElement.clientWidth * Number(temp) / 100;
            }
            if(width > 970) {
                width = 970;
            }
            //alert(width);
            $('#' + newId).setGridWidth(width);
        }
        grid.jqGrid('navGrid', '#' + this.getIdByPageId(config.pagerId), {edit:false, add:false, search:false, del:false, excel:true},
            {mtype:"POST", closeAfterAdd:true, reloadAfterSubmit:true});

        if (config.autoSize) {
            $('#' + newId).setGridWidth(document.documentElement.clientWidth - 4, true);
            $(window).bind('resize', function() {
                $('#' + newId).setGridWidth(document.documentElement.clientWidth - 4, true);
            });
        }

        // 初始化工具栏
        this.buildToolbar(id, config.toolbars, config.appendToolbar);

        this._combineHeader(grid, options.columns);

    },

    _combineHeader: function(grid, columns) {
        var col, key, groupDesc, groupHeader = [], groupHeaderPlus = [];
        for (key in columns) {
            col = columns[key];
            if (typeof col['groupHeader'] === 'object') {
                groupDesc = col['groupHeader'];
                groupHeader.push({
                    startColumnName:col.name, numberOfColumns:groupDesc.colspan, titleText:groupDesc.text
                });
            }

            if (typeof col['groupHeaderPlus'] === 'object') {
                groupDesc = col['groupHeaderPlus'];
                groupHeaderPlus.push({
                    startColumnName:col.name, numberOfColumns:groupDesc.colspan, titleText:groupDesc.text
                });
            }
        }

        if (groupHeader.length > 0) {
            grid.jqGrid('setGroupHeaders', {
                useColSpanStyle:true,
                groupHeaders: groupHeader
            });
        }

        if (groupHeaderPlus.length > 0) {
            grid.jqGrid('setComplexGroupHeaders', {
                complexGroupHeaders:groupHeaderPlus
            });
        }
    },

    _jqGridRowMerger: function(gridName, cell, groupedColumns) {
    	var CellName = cell.name;
        //得到显示到界面的id集合
        var grid = $("#" + gridName), mya = grid.getDataIDs(), length = mya.length, toBeRemove = [], i, j, value1;
        var checkEqual = function(startRow, endRow, value1) {
            var equal = false,
                // value1 = grid.jqGrid('getRowData', mya[startRow]),
                value2 = grid.jqGrid('getRowData', mya[endRow]);
            
            if (cell.single) {
            	equal = (value1[CellName] == value2[CellName]);
            } else {
            	$.each(groupedColumns, function() {
                    equal = (value1[this.name] == value2[this.name]);
                    if (!equal) {
                        return false;
                    }
                });
            }
            return equal;
        };

        //当前显示多少条
        for (i = 0; i < length; i++) {
            //定义合并行数
            var rowSpanTaxCount = 1;
            value1 = grid.jqGrid('getRowData', mya[i]);
            
            for (j = i + 1; j <= length; j++) {
                //和上边的信息对比 如果值一样就合并行数+1 然后设置rowspan 让当前单元格隐藏
                if (checkEqual(i, j, value1)) {
                    rowSpanTaxCount++;
                    grid.setCell(mya[j], CellName, '', { display:'none' });
                    toBeRemove.push("#" + CellName + mya[j]);
                    // $("#" + CellName + mya[j]).remove();
                } else {
                	i = j - 1;
                    rowSpanTaxCount = 1;
                    break;
                }
                $("#" + CellName + mya[i]).attr("rowspan", rowSpanTaxCount);
            }
        }

        return toBeRemove;
    },

    /**
     * 初始化工具栏
     * @param gridId
     * @param toolbar 工具栏对象
     * @param append 是否直接增加
     */
    buildToolbar:function (gridId, toolbar, append) {
        toolbar = toolbar || {};
        gridId = this.getIdByPageId(gridId);

        if (toolbar.display == false) {
            $('#t_' + gridId).hide();
            return;
        }

        // toolbar设置为top或bottom, 如果设置为both的话, button只显示在上面的工具条上
        var toolbarContainer = $('#t_' + gridId);
        var buttons = toolbar.buttons;

        toolbarContainer.addClass("toolbar");
        if (append !== true) {
            toolbarContainer.empty();
        }
        for (var btn in buttons) {
            var button = $('<button type="button"/>').text(btn).button();
            button.bind('click', buttons[btn].createDelegate(this, [$('#' + gridId)], 'falsy'));
            toolbarContainer.append(button);
        }
    },

    /**
     * 响应双击事件
     * @param rowid
     * @param iRow
     * @param iCol
     * @param e
     */
    dblClickHandler:function (rowid, iRow, iCol, e) {
        this.showDialog(null, rowid, true);
    },

    /**
     * 生成jqGrid列信息
     * @param colConfig
     * @return {Object}
     */
    _buildColumeModel:function (colConfig) {
        var colNames = [], colModel = [], groupedCols = [];
        colConfig = colConfig || {};

        for (var col in colConfig) {
            colConfig[col]['tagName'] = col;
            colModel.push(colConfig[col]);
        }

        var form = $('#' + this.getIdByPageId(this.config.formConfig.formId));
        var columns = form.find('input[cmname],select[cmname],textarea[cmname]');
        $.each(columns, function(k, v) {
            colModel.push({
                name: $(v).attr('cmname') || $(v).attr('name'),
                index: $(v).attr('cmname') || $(v).attr('name'),
                width: $(v).attr('cmwidth'),
                cmindex: $(v).attr('cmindex'),
                tagName: $(v).attr('displayName') || $(v).parentsUntil('td').parent().prev().find('.inputname').html().replace(new RegExp("[:：]"), '')
            });
        });
        colModel.sort(function(a, b) {
            var numA, numB;
            numA = parseInt(a.cmindex);
            numB = parseInt(b.cmindex);

            if (numA < numB) return -1;
            if (numA > numB) return 1;
            return 0;
        });
        $.each(colModel, function(k, v) {
            if (v.grouped) {
                groupedCols.push({name:v.name, single: v.single});
                v.cellattr = function (rowId) {
                    return 'id=\'' + v.name + rowId + "\'";
                }
            }
            colNames.push(v.tagName);
            delete v.tagName;
        });

        return {
            colNames:colNames,
            colModel:colModel,
            groupedCols: groupedCols
        };
    },

    /**
     * 初始化表单
     * @param id 表单ID
     * @param options 表单属性
     */
    initForm:function (id, options) {
    	options = options || {};
        id = id || this.config.formConfig.formId;
        id = this.getIdByPageId(id);
        var form = $('#' + id);
        var self = this;
		var config = $.extend(true, {}, this.config.formConfig, options);

		config.url = this.getFullPath(config.actionUrl);
		config.success = function(responseText) {
            form.data('submit', false);
			self.saveSuccess.call(self, responseText, form, config.successCallback);
		};

        //表单配置
        form.find('table:first').addClass('table_border');
        form.ajaxForm();
        form.submit(function () {
            var attachmentObj = $(this).find('div[type=file]');
            if (attachmentObj.length > 0) {
                var result = attachmentObj.attachment('validate');
                if (result === true) {
                    attachmentObj.attachment('putValue2Form', $(this).attr('id'));
                    $(this).ajaxSubmit(config);
                }
            } else {
                $(this).ajaxSubmit(config);
            }

            return false;
        });
        form.bind('baseFormSubmit', function(event, dlgId) {
            if (form.data('submit')) {
                log('blocked the dummy submit');
                return;
            }
            form.data('dlgId', dlgId);
            form.trigger('submitBefore', [form]);
            form.submit();
        });

        // 附件
        form.find('div[type=file]').attachment({});

        //表单验证
        form.validate(this.buildValidateRules(form.find('input[validator],select[validator],textarea[validator]')));

        if ((config.readonly + '') != 'true') {
            // 日期控件
            var dateInputs = $('input[customtype=date]', form);
            $.each(dateInputs, function(k, v) {
                $(v).attr('readonly', 'true');
                $(v).datepicker({
                    dateFormat: $(v).attr('dateFormat') || 'yy-mm-dd',
                    beforeShow: function (i, e) {
                        var z = jQuery(i).closest(".ui-dialog").css("z-index") + 4;
                        e.dpDiv.css('z-index', z);
                    }
                });
            });

            if (config.dialog) {
                // dialog
                $('input[fetch],textarea[fetch]').getReturnValue();
            }
        }
        // 加载数据
        if (config.id) {
            $('#' + this.getIdByPageId(this.config.formConfig.formId)).find('div[type=file]').attachment('load', config.id, config.readonly);
            var operation = (config.readonly ? 'view' : 'modify');
            var param = $.extend({}, config.dataLoader.param, {'id': config.id, operation: operation});
            this.mySyncAjax(this.getFullPath(config.dataLoader.url), param, config.dataLoader.loader.createDelegate(self, [config.readonly, ''], 1));
        }
        
        form.trigger('finishInitForm', [form]);
    },

    /**
     * 显示信息对话框
     *
     * @param dialogConfig 对话框配置
     * @param id 业务ID
     * @param readonly 是否只读
     */
    showDialog: function (dialogConfig, id, readonly) {
        var self = this;
        readonly = ((readonly + '') == 'true');
        dialogConfig = dialogConfig || {};
        var zIndex="";
        var dialogParent = window;
        try{
            dialogParent = (frameElement && frameElement.api ? frameElement.api.opener.$.lhgdialog.focus : $.lhgdialog.focus);
            if(window.top && window.top.$ !=undefined && window.top.$.lhgdialog){
                zIndex=window.top.$.lhgdialog.setting.zIndex + 100;
            }else{
                zIndex=frameElement && frameElement.api ? frameElement.api.opener.$.lhgdialog.setting.zIndex + 100 : $.lhgdialog.setting.zIndex + 100;
            }
        }catch(error) {
            // iframe 跨域
        }
        var config = {
            // id: self.getIdByPageId(self.config.dialogConfig.dialogId),
            content:$.format('url:{0}', dialogConfig.url || self.config.dialogConfig.url),
            parent: dialogParent,
            zIndex: zIndex,
            lock: true,
            min:false,
            max:false,
            resize:false,
            esc:true,
            param: {
                id: id,
                readonly: readonly,
                pageId: self.pageId,
                actionNameSpace: self.config.actionNameSpace
            }
        }
        config = $.extend(config, this.config.dialogConfig, dialogConfig);
        if(window.top && window.top.$ !=undefined && window.top.$.lhgdialog){
        	window.top.$.lhgdialog.setting.zIndex = config.zIndex;
        }
        
        // 这里负责将默认的参数进行传递
        if(!config.param.actionNameSpace || self.config.actionNameSpace) {
        	config.param.actionNameSpace =  self.config.actionNameSpace;
        }
        if(!config.param.pageId || self.pageId) {
        	config.param.pageId =  self.pageId;
        }
        if(id) {
        	config.param.id =  id;
        } else {
        	if (this.config.dialogConfig.param && this.config.dialogConfig.param.id) {
        		delete this.config.dialogConfig.param.id;
        	}
        }
        if(!config.param.readonly || readonly || (config.param.readonly && !readonly)) {
        	config.param.readonly =  readonly;
        }
        if (!config.id) {
            config.id = 'dlg_' + new Date().getTime();
        }
        
        if (!readonly && !config.ok) {
            config.ok = function(win, dlg) {
                return self.triggerDialogEvent(dlg, 'baseFormSubmit', [config.id]);
            };
        }
        // 如果readonly则取消确定按钮
        if(readonly) {
        	config.cancelVal = "关闭";
        	delete config.ok;
        }
        // 去掉确定取消按钮
        if (config.noBtn) {
        	delete config.ok;
        	delete config.cancel;
        }

        var title = (readonly ? '查看' : '修改');
        title = (id ? title : '添加');
        if (config.title) {
            title = $.format(config.title, title)
        }

        // 打开对话框前调用函数

        config.init = function(iWin, top) {
            if (arguments.length == 1) {
                // 本页面时不执行以下方法
                return;
            }
            self.parentWin = top;

            // 这里为了在子窗口能够bind上该事件，所以延迟进行trigger事件
            iWin.$('body').ready(function(){
            	setTimeout(function(){
                    iWin.$('body').trigger('initPage', [config.id, this, readonly]);
                },5);
            })


            if (typeof config.beforeShowDialog == 'function') {
                config.beforeShowDialog.call(self, this);
            }
        };
        // 如果配置了动画的滑动效果
        if(config.slide) {
            var opts = config,
                api, aConfig, hide, wrap, left,
                duration = opts.duration || 800;

           config.init = function(iWin, top) {
               if (arguments.length == 1) {
                   // 本页面时不执行以下方法
                   return;
               }
               self.parentWin = top;

               // 这里为了在子窗口能够bind上该事件，所以延迟进行trigger事件
               setTimeout(function(){
                   iWin.$('body').trigger('initPage', [config.id, this, readonly]);
               },5);

               if (typeof config.beforeShowDialog == 'function') {
                   config.beforeShowDialog.call(self, this);
               }

               api = this;
               aConfig = api.config;
               wrap = api.DOM.wrap;
               left = parseInt(wrap[0].style.left);
               hide = left + wrap[0].offsetWidth;

               wrap.css('left', hide + 'px')
                   .animate({left:left + 'px'}, duration, function () {
                       //opts.init && opts.init.call(api, here);
                   });
           };
        }

        return $.lhgdialog(config).title(title).show();
    },

    /**
     * 关闭所有的弹出窗口
     */
    closeAllDialog: function() {
        try{
            var list = window.top.$.lhgdialog.list;
            for (var i in list) {
                if (list[i])
                    list[i].close();
            }
        }catch(error){}
    },

    /**
     * 对话框
     */
    showConfirm: function(msg, okCallback , cancleCallback){
        $.lhgdialog.confirm(msg, okCallback , cancleCallback);
    },

    /**
     * 触发对话框中自定义事件
     *
     * @param dlg
     * @param event
     * @param args 事件响应函数参数, 数组
     * @return {Boolean}
     */
    triggerDialogEvent: function(dlg, event, args) {
        var obj = dlg.$('#' + this.getIdByPageId(this.config.formConfig.formId));
        obj.trigger(event, args);
        return false;
    },

    /**
     * javascript ajax异步调用
     * @param url
     * @param param
     * @param callback
     */
    myAjax:function (url, param, callback) {
        var self = this;
        $.ajax({
            type:"POST",
            url: this.getFullPath(url),
            data: param,
            dataType: "json",
            cache:false,
            success:function (data) {
                if (typeof callback == 'function') {
                    callback.call(self, data);
                }
            }
        });
    },
    
    /**
     * javascript ajax 同步调用
     * @param url
     * @param param
     * @param callback
     */
    mySyncAjax:function (url, param, callback) {
        var self = this;
        $.ajax({
            type:"POST",
            url: this.getFullPath(url),
            data: param,
            dataType: "json",
            cache:false,
            async: false, //ajax同步
            success:function (data) {
                if (typeof callback == 'function') {
                    callback.call(self, data);
                }
            }
        });
    },

    /**
     * 设置对话框的按钮
     * @param dialogId
     * @param operation before-将param中的按钮添加到现有按钮前, after-将param中的按钮添加到现有按钮后, reset-用param中的按钮替换现在按钮
     * @param param 按钮
     */
    setDialogBtn: function(dialogId, operation, param) {
        dialogId = this.getIdByPageId(dialogId);
        var dialog = $.lhgdialog.list[dialogId];

        switch (operation) {
            case 'before':
                var buttons = dialog.getAllButtons();
                $.each(buttons, function() {
                    dialog.removeButton(this.id);
                });
                dialog.button(param);
                $.each(buttons, function(k, v) {
                    dialog.button(v);
                });
                break;
            case 'after':
                dialog.button(param);
                break;
            case 'reset':
                var buttons = dialog.getAllButtons();
                $.each(buttons, function() {
                    dialog.removeButton(this.id);
                });
                dialog.button(param);
            default:
                break;
        }
    },

    /**
     * 删除记录
     *
     * @param gridId
     * @param deleteUrl
     */
    deleteRecord:function (gridId, deleteUrl) {
    	
        gridId = this.getIdByPageId(gridId || this.config.jqGridConfig.jqGridId);
        var self = this;
        var selectedIds = $('#' + gridId).jqGrid("getGridParam", "selarrrow");
        if (selectedIds.length == 0) {
            this.msgBox('请选择要删除的记录');
            return;
        }
        
        if (typeof this.config.jqGridConfig.beforeDelete == 'function') {
    		if(this.config.jqGridConfig.beforeDelete.call(self, gridId)){
    			$.lhgdialog.confirm('删除后将不可恢复, 确认要删除该记录吗?', function() {
    	            self.myAjax(self.getFullPath(deleteUrl || self.config.formConfig.deleteUrl), {ids:selectedIds.join()}, function (data) {
    	                if (data.message == 'OK') {
    	                    $('#' + gridId).trigger('reloadGrid');
    	                } else {
    	                    $.alerts.alert('删除失败', '操作提示');
    	                }
    	            });
    	        });
    		}
        }
        
        
    },

    /**
     * 保存成功后的回调函数
     *
     * @param responseText
     * @param $form
     * @param callback
     */
    saveSuccess:function (responseText, $form, callback) {
        var self = this;
        (frameElement && frameElement.api ? frameElement.api : $.lhgdialog.focus).hide();
        (frameElement && frameElement.api ? frameElement.api.opener: window).$('#' + this.getIdByPageId(this.config.jqGridConfig.jqGridId)).trigger('reloadGrid');
        if (typeof callback == 'function') {
            callback.createDelegate(self, [frameElement.api.opener], true).call(self);
        }
        var noshowalter = $form.data('noshowalter');
        if(noshowalter && noshowalter == 'true'){
        	// dummy
        }else{
	        this.msgBox('保存成功', function() {
	        	(frameElement && frameElement.api ? frameElement.api : $.lhgdialog.focus).close();
	        });
        }
    },

    /**
     * 获取带pageId的ID标识
     * @param id
     * @return
     */
    getIdByPageId:function (id) {
        return id + this.pageId;
    },

    /**
     * 加载数据到表单中
     *
     * @param formId
     * @param data 如果为url时,自动加载URL获取数据
     * @param isView true - 只读
     */
    loadData2Form: function(formId, data, isView, prefix) {
        var self = this, t, value, form = $('#' + formId), plainData = this.plainObject(data, prefix);
        isView = ((isView + '') == 'true');
        if (form.attr('id') != formId) {
            form = $('#' + this.getIdByPageId(formId));
        }
        // 处理红色标记
        if ((isView + '') == 'true') {
            form.find('.font_red').hide();
        } else {
            form.find('.font_red').show();
        }

        if (typeof data == 'string') {
            $.ajax({
                url: data,
                dataType: 'json',
                method: 'POST',
                success: function(json) {
                    self.loadData2Form.call(self, formId, json, isView, prefix);
                }
            });

            return;
        }

        $.each(form.find('input,textarea,select'), function(k, v) {
            t = v.type;
            value = plainData[v.name];
            if (typeof value != 'undefined') {
                switch (t) {
                    case 'checkbox':
                        $.each(value.split(','), function() {
                            // 使用trim防止保存时出现空格
                            if (v.value == $.trim(this)) {
                                $(v).attr('checked', true);
                                //多选时，类型为其他时，显示手动输入信息框
                                	$("#other").show();
                            }
                        });
                        break;
                    case 'radio':
                        if (v.value == value) {
                            $(v).attr('checked', true);
                        }
                        break;
                    default:
                        $(v).val(value);
                        break;
                }
            }

            if (t == 'hidden') {
                return true;
            }
        });
        
        form.trigger('finishLoadData', [data]);
        if (isView) {
        	form.find('input:not(:hidden),textarea,select').each(function() {
            	var text = null, type;
            	switch(this.tagName) {
            	case 'INPUT':
            		type = this.type;
            		if (!this.checked) {
            			if (type == 'radio') {
            				$(this).next().hide();
            			} else if (type == 'checkbox') {
            				$(this).prev().hide();
            			} else {
            				text = $(this).val();
            			}
            		}
            		break;
            	case 'SELECT':
            		text = $(this).find('option[value=' + $(this).val() +']').text();
            		if(text==="请选择") text="";
            		break;
            	case 'TEXTAREA':
            		text = $(this).val();
            	}
            	
            	$(this).hide();
            	if (text) {
            		if($(this).attr('id')=='other'){
            			text="("+text+")";
            		}
            		$(this).after($('<span/>', {
            			inputId: this.id,
            			inputName: this.name,
                		'class': 'readonly'
                	}).text(text));
            	}
            });
        }
    },

    /**
     * 加载数据到只读页面
     *
     * @param id
     * @param data
     */
    loadData2View: function(id, data) {
        var self = this, view = $('#' + id), plainObj = self.plainObject(data);
        if (view.attr('id') != id) {
            view = $('#' + this.getIdByPageId(id));
        }

        if (typeof data == 'string') {
            $.ajax({
                url: data,
                dataType: 'json',
                method: 'POST',
                success: function(json) {
                    self.loadData2View.call(self, formId, json);
                }
            });
        }
        $.each(view.find('label.readonly'), function() {
            $(this).text(plainObj[this.name]);
        });

        view.trigger('finishLoadData', [data]);
    },

    /**
     * 构建验证规则
     *
     * @param elements
     * @return {Object}
     */
    buildValidateRules: function(elements) {
        var rules = {rules:{}, messages:{}};
        $.each(elements, function(k, v) {
            rules['rules'][v.name] = eval('(' + v.validator + ')');
            rules['messages'][v.name] = v.message;
            
            if (rules['rules'][v.name]&&rules['rules'][v.name].required) {
            	if($(v).parent().next().text() == "*"){
            		$(v).parent().next().replaceWith("<div class='font_red'>*</div>");
            	}else{
            		$(v).parent().after("<div class='font_red'>*</div>");
            	}
            }
        });
        return rules;
    },

    /**
     * 将对象扁平化
     *
     * @param obj
     * @param prefix
     * @param plainObj
     * @return {*}
     */
    plainObject: function(obj, prefix, plainObj) {
        plainObj = plainObj || {};
        prefix = prefix || '';

        var _getName = function(s) {
            return (prefix ? prefix + '.' + s : s);
        }

        for (var s in obj) {
            if (typeof obj[s] == 'object') {
                this.plainObject(obj[s], _getName(s), plainObj);
            } else {
                plainObj[_getName(s)] = obj[s];
            }
        }

        return plainObj;
    },

    /**
     * 获取完整的URL
     * @param path
     */
    getFullPath:function (path) {
        if (/^\/.*$/.test(path)) {
            return path;
        }
        return rootPath + '/action/Struts_' + this.config.actionNameSpace + '_' + path;
    },

    /**
     * 显示提示信息
     *
     * @param msg
     * @param callback
     */
    msgBox:function (msg, callback, parent , noclose) {
        var dialog;
        if (window.top && window.top.$) {
            dialog = window.top.$.lhgdialog
        } else {
            dialog = $.lhgdialog;
        }

        if (typeof callback == 'function') {
        	callback.call(this);
        }
        if(noclose) {
            dialog.alert(msg || '操作成功', null, parent).title('操作提示');
        }else{
            dialog.alert(msg || '操作成功', null, parent).title('操作提示(2秒后自动关闭)').time(2);
        }
    },

    /**
     * 显示操作结果
     *
     * @param data
     */
    showOperationResult: function(data, successMsg, failMsg) {
        var $ = (frameElement && frameElement.api ? frameElement.api.opener.$ : $);
        if (data.message == 'OK') {
            this.msgBox(successMsg ? successMsg : '操作成功!', null, $.lhgdialog.focus).time(3);
        } else {
            this.msgBox(failMsg ? failMsg : '操作失败!', null, $.lhgdialog.focus);
        }
    },

    tabs: function(options) {
        var config = {
            cache: true,
            el: 'tabs',
            tabArray: [
                {
                    title: '空白页面',
                    url: 'about:blank;'
                }
            ],
            show: function(event, ui) {
                var $panel = $(ui.panel);
                var iframe = $panel.find('iframe');
                var frameConfig = {
                    allowTransparency: "yes",
                    style: "'background-color-transparent': 'transparent';'overflow':'scroll';'overflow-x':'hidden'",
                    scrolling: 'auto',
                    width: '100%',
                    height:'100%',
                    framespacing: 0,
                    frameborder: "no",
                    border: 0,
                    marginheight: 0,
                    marginwidth: 0
                };
                
                tabs = $('#' + tabId);
                iframe[0].tabs = tabs;
                if(config.autowidth && config.autowidth == 'false') {
                	// 如果设置了autowidth = false , 则不调整宽度
                	$.extend(frameConfig, {
                        src: $panel.attr('url'),
                        height: tabs.height()-35,
                        width: '100%'
                    });
                }else{
                	if(document.documentElement.clientHeight != 0) {
	                    $.extend(frameConfig, {
	                    	src: $panel.attr('url'),
	                        height: document.documentElement.clientHeight-35,
	                        width: document.documentElement.clientWidth
	                    });
                	}else{
                		$.extend(frameConfig, {
	                    	src: $panel.attr('url'),
	                        height: '400px',
	                        width: '100%'
	                    });
                	}
                }
                if (config.cache && $panel.attr('shown') == 'true') {
                    return;
                }
                if (typeof iframe.attr('src') == 'undefined') {
                    iframe.attr(frameConfig);
                } else {
                    $panel.empty();
                    iframe = $('<iframe framespacing="0" frameborder="NO" border="0" marginheight="0"  marginwidth="0" style="overflow:scroll;overflow-x:hidden"></iframe>', frameConfig);
                    iframe.attr(frameConfig);
                    $panel.append(iframe);
                }

                $panel.attr('shown', true).show();
            }
        };
        config = $.extend(true, config, options || {});

        var self = this, tabId = this.getIdByPageId(config.el);
        var tabs = $('#' + tabId), bodyWidth = $('body').width(), bodyHeight = $('body').height();
        var tabStr = '<li><a href="#{0}">{1}</a></li>';
        var tabContentStr = '<div id="{0}" url="{1}" style="display: none;"><iframe framespacing="0" frameborder="NO" border="0" marginheight="0"  marginwidth="0" style="overflow:scroll;overflow-x:hidden"></iframe></div>';

        if (tabs.length == 1) {
            var ul = $('<ul/>');
            var contentId;
            $.each(config.tabArray, function(k) {
                contentId = tabId + '_' + k;
                ul.append($.format(tabStr, contentId, this.title));
                tabs.append($.format(tabContentStr, contentId, this.url));
            });

            tabs.prepend(ul);
            tabs.tabs(config);
            if(config.autowidth && config.autowidth == 'false') {
            	// 如果设置了autowidth = false , 则不调整宽度
            }else{
	            tabs.width(bodyWidth - 5).height(bodyHeight-5);
	            $(window).bind('resize', function() {
	                tabs.width($(window).width() - 5).height($(window).height() -5);
	                tabs.find('iframe').width($(window).width() - 10).height($(window).height() - 37);
	            });
            }
        } else {
            log('tabsID不唯一');
        }
    },

    /**
     * 向现有的Tab组中添加新的tab
     *
     * @param tabs tabs对象
     * @param url 显示的URL
     * @param lable 显示的标题
     * @param index 新加入的tab的位置, 从0开始
     * @param show 加入后是否立即显示
     */
    addTab: function(tabs, url, lable, index, show) {
        var id = tabs.attr('id'),
            length = tabs.tabs('length'),
            ul = tabs.find('ul'),
            contentId = id + '_' + length,
            lis = ul.find('li'),
            list = tabs.find('div[url]'), exist = false;


        list.each(function(k) {
            if (url == this.url) {
                exist = true;

                // 选择tab
                tabs.tabs('select', k);
                return false;
            }
        });

        if (exist) {
            return;
        }

        var tabStr = '<li><a href="#{0}">{1}</a></li>';
        var tabContentStr = '<div id="{0}" url="{1}" style="display: none;"><iframe></iframe></div>';

        if (index == undefined || index >= lis.length) {
            ul.append($.format(tabStr, contentId, lable));
            tabs.append($.format(tabContentStr, contentId, url));
        } else {
            $($.format(tabStr, contentId, lable)).addClass("ui-state-default ui-corner-top").data("destroy.tabs", true).insertBefore(lis[index]);
            $($.format(tabContentStr, contentId, url)).addClass("ui-tabs-panel ui-widget-content ui-corner-bottom ui-tabs-hide").insertBefore(list[index]);
        }

        tabs.tabs('add', '__to_be_remove', 'a');
        tabs.tabs('remove', '__to_be_remove');

        if (show) {
            tabs.tabs('select', contentId);
        }
    }
});

var unDictData = {}, dictData = {};
$.extend(true, com.cenmobile.base, {
    dictHelper: {
        /**
         * 根据code加载字典数据
         *
         * @param codes 字典代码, 数组形式
         * @param callback 回调函数
         */
        loadDict: function(codes, callback) {
            var params = 'dummy=0';
            for (i = 0; i < codes.length; i++) {
                params += '&dictCodes=' + codes[i]
            }

            $.ajax({
                type: 'post',
                url: rootPath + '/action/Struts_common_Dict_load.action',
                data: params,
                dataType : "json",
                cache : false,
                async:false,
                success: function(data, textStatus) {
                    for (code in data) {
                        dictData[code] = data[code];
                    }
                    if (callback) {
                        callback();
                    }
                }
            });
        },

        /**
         * 根据code返回字典数据
         *
         * @param dictCode
         */
        getDictDataByCode: function(dictCode) {
            return dictData[dictCode];
        },

        /**
         * 根据code返回相应的formatter
         *
         * @param code 字典代码
         * @param myFormatter 自定义的formatter, 会传入以下三个参数:
         *              val - 字典名称
         *              options - 同formatter的options
         *              rowData - 同formatter的rowData
         */
        getDictFormatter: function(code, myFormatter, myEmptyValue) {
            if (!unDictData[code]) {
                unDictData[code] = {};
            }

            var _self = this;

            var formatter = function(val, options, rowData, act) {
                var data = _self.getDictDataByCode(code);
                if (val) {
                    var result;
                    if (data) {
                        result = data[val];
                    }
                    result = result ? result : val; // 未找到对应的数据字典时, 返回原始数据
                    if (myFormatter) {
                        result = myFormatter(val, result, rowData);
                    }
                    unDictData[code][result] = val;
                    return result;
                }

                return myEmptyValue?myEmptyValue:'&nbsp;';
            };

            return formatter;
        },

        getUnFormatter: function(code) {
            var unformatter = function(val) {
                return unDictData[code][val];
            };

            return unformatter;
        }
    }
});

/**
 * 兼容以前的弹出框
 */
function hAlert() {
    var dialog;
    if (window.top && window.top.$) {
        dialog = window.top.$.lhgdialog
    } else {
        dialog = $.lhgdialog;
    }

    if (typeof callback == 'function') {
        callback.call(this);
    }
    if(noclose) {
        dialog.alert(msg || '操作成功', null, parent).title('操作提示');
    }else{
        dialog.alert(msg || '操作成功', null, parent).title('操作提示(2秒后自动关闭)').time(2);
    }
}
(function($) {
    var _cacheData = {};

    /**
     * 加载字典数据
     * @param code
     * @param paramType
     * @param callback
     */
    var _loadData = function(code, paramType, callback) {
    	
        var data = _cacheData[code];
        if (data) {
            callback(data);
            return;
        }
        var params = "";
        var url = undefined;
        switch(paramType) {
            case '1': 
                url = rootPath + '/action/Struts_common_Dict_loadDict.action';
                params = {dictCodes : code};
                break;
            case '2': 
                url = rootPath + '/action/Struts_common_Dict_loadDict.action';
                params = {dictCodes : code};
                break;
        }
        
        $.ajax({
            type: 'post',
            url: url,
            data: params,
            dataType : "json",
            cache : false,
            success: function(data, textStatus) { 
                _cacheData[code] = data;
                callback(data);
            }
        });
    }

    var _loadOptions = function($select, code, callback) {
        var paramType = $select.attr('paramType');
        var start = parseInt(code);
        if (!code) {
            if (callback) {
                callback();
            }
            return;
        }
        _loadData(code, paramType, function(data){
            $select.empty();
            $select.append('<option value="">请选择</option>');
            var objOption = document.createElement("option");
            for (var i = 0; i < data.length; i++) {
                var curData = data[i];
                 switch(paramType) {
                    case '1':               
                        $select.append('<option title="'+curData.displayName+'" value="' + curData.name + '">' + curData.displayName + '</option>');
                        break;
                    case '2': 
                        $select.append('<option title="'+curData.displayName+'" value="' + curData.name + '">' + curData.displayName + '</option>');
                        break;
                }
                
            }
            if (callback) {
                callback();
            }
        })
    }

    /**
     * 级联select
     * @param options
     */
    $.fn.bindSelect = function(options) {
        var select = $(this)[0];
        if (!select) {
            return;
        }
        $select = $(select);
        
        $select.bind('change', function(event, opt) {
            var $targetSelect = $(options.target);
            var _value, _callback;
            if (!opt) {
                _value = event.srcElement.value;
            } else {
                _value = opt.preLoad;
                _callback = opt.callback;
            }
            _loadOptions($targetSelect, _value, _callback);
            _callback = undefined;
        });
        
        if (options.preLoad) {
            $select.trigger('change', options);
        }

        _loadOptions($select, options.code, options.callback);
        return $(options.target);
    }
})(jQuery);