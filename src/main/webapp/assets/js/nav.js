/**
 * Author: Bill Lv<ideaalloc@gmail.com>
 * Date: 2015-02-11
 */
webix.protoUI({
    name:"navbar",
    defaults:{
        height:1,
        css:"navbar",
        template:"<div class='text'><span>#text#</span></div>"
    },

    value_setter:function(value){
        var index = 0;
        for (var i = 0; i < this.demos.length; i++)
            if (this.demos[i].name == value)
                index = i;

        this.data = this.demos[index];
        this.data.next = this.demos[(index+1) % this.demos.length];
        this.data.prev = this.demos[(index -1 + this.demos.length) % this.demos.length];
    },
    demos:[
        { name:"Freeour", 	link:"/", text:"飞窝俱乐部" }
    ]
}, webix.ui.template );