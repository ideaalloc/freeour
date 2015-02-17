/**
 * Author: Bill Lv<ideaalloc@gmail.com>
 * Date: 2015-02-09
 */
var React = require('react');
var Activities = require('./components/Activities');

var listActivities = function(userId) {
    React.render(
        <Activities userId={userId} />,
        document.getElementById('activities')
    );
}
