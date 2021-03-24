String.prototype.yyyy_mm_dd = function() {
    var date = new Date();
    var d = date.getDate();
    var m = (date.getMonth()+1>9)?(date.getMonth()+1):'0'+(date.getMonth()+1);
    var y = date.getFullYear();
    var defaultDate = y+'-'+m+'-'+d;
    return defaultDate;
};