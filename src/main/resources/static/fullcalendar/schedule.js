(function () {
    var jsonData = jsonList.replace(/&quot;/g,'"');
    var jsonConvertList = JSON.parse(jsonData);
    //var defaultDate = String.prototype.yyyy_mm_dd();
    //console.log("jsonData "+jsonData);
    //console.log("defaultDate "+defaultDate);
    document.addEventListener('DOMContentLoaded', function() {
        var calendarEl = document.getElementById('calendar');
        var calendar = new FullCalendar.Calendar(calendarEl, {
            plugins: [ 'bootstrap', 'interaction', 'dayGrid', 'timeGrid', 'list' ],
            header: {
                left: 'prev,next today',
                //right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
                center: 'title',
                right: 'dayGridMonth,listMonth'
            },
            defaultDate: new Date(),
            weekNumbers: true,
            weekNumbersWithinDays: true,
            weekNumberCalculation: 0,
            locale: 'ko',
            //allDayDefault: true,
            businessHours: true, // display business hours
            editable: false,
            weekNumbers: false,
            eventLimit: true, // allow "more" link when too many events
            events: eval(JSON.stringify(jsonConvertList.item))         
        });
        calendar.render();
    });
})();