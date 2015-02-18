/**
 * Author: Bill Lv<ideaalloc@gmail.com>
 * Date: 2015-02-16
 */
var ReactPropTypes = React.PropTypes;

var ActivitiesBox = React.createClass({
    loadFromServer: function () {
        $.ajax({
            url: '/activities',
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                this.setState({data: data});
            }.bind(this),
            error: function (xhr, status, err) {
            }.bind(this)
        });
    },

    getInitialState: function () {
        return {data: []};
    },

    componentDidMount: function () {
        this.loadFromServer();
    },

    render: function () {
        return (
            <ActivityList data={this.state.data} />
        );
    }
});

var PeopleList = React.createClass({
    render: function () {
        var peopleNodes = this.props.data.map(function (userId, index) {
            var person = React.createElement(
                "img",
                {className: "ui avatar image", src: "/users/" + userId + "/avatar"}
            );

            return person;
        });
        if ($.isEmptyObject(peopleNodes)) {
            return (
                <span></span>
            );
        }
        return (
            <div className="extra content">
                <div className="right floated author">
            {peopleNodes}
                </div>
            </div>
        );
    }
});

var getUserId = function () {
    return $('#userId').val();
}

var joined = function (activityId, userId) {
    var url = '/activities/' + activityId + '/users/' + userId;
    return $.ajax({
        type: "GET",
        url: url,
        async: false
    }).responseText;
}

var getUsersNum = function (activityId) {
    var url = '/activities/' + activityId + '/users/count';
    return $.ajax({
        type: "GET",
        url: url,
        async: false
    }).responseText;
}

var getUserIds = function (activityId) {
    var url = '/activities/' + activityId + '/userids';
    return $.parseJSON($.ajax({
        type: "GET",
        dataType: "json",
        url: url,
        async: false
    }).responseText);
}

var ActivityList = React.createClass({
    render: function () {
        var activityNodes = this.props.data.map(function (activity, index) {
            var userId = getUserId();
            var joinedV = joined(activity.id, userId);
            var usersNum = getUsersNum(activity.id);
            var userIds = getUserIds(activity.id);

            var joinElem = (joinedV === 'true') ?
                React.createElement("button", {
                        type: "submit",
                        name: "activityId",
                        value: activity.id,
                        className: "right floated ui disabled button"
                    },
                    React.createElement("i", {className: "user icon"}),
                    "已报名"
                ) :
                React.createElement("button", {
                        type: "submit",
                        name: "activityId",
                        value: activity.id,
                        className: "right floated ui button"
                    },
                    React.createElement("i", {className: "user icon"}),
                    "报名"
                );
            return (
                React.createElement("div", {className: "ui card"},
                    React.createElement("div", {className: "content"},
                        joinElem,

                        React.createElement("div", {className: "header"}, activity.title),
                        React.createElement("div", {className: "meta"}, activity.startTime),
                        React.createElement("div", {className: "meta"}, activity.address),

                        React.createElement("div", {className: "description"},
                            React.createElement("p", null, activity.description)
                        )
                    ),
                    React.createElement("div", {className: "extra content"},
                        React.createElement("i", {className: "check icon"}),
                        usersNum, " 人参加"
                    ),
                    React.createElement(PeopleList, {data: userIds})
                )
            );
        });
        return (
            <div className="ui two doubling cards">
                {activityNodes}
            </div>
        );
    }
});

var listActivities = function () {
    React.render(
        <ActivitiesBox />,
        document.getElementById('activities')
    );
};
