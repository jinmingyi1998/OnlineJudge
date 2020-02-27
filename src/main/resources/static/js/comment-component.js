comment_group = Vue.component("comment-group", {
    props: ["comments", "level"],
    template: `
<div class="ui threaded comments">
    <div class="comment" v-for="s in comments" v-if="s.fatherId==null||level!=0">
        <div class="content">
            <a class="author">{{s.user.name}}</a><div class="metadata"><span class="data">{{s.normalPostTime}}</span></div>
            <div class="md-text" style="padding: 0"><textarea style="display: none;">{{s.text}}</textarea></div>
            <div class="actions"><a class="reply" v-on:click="s.reply_action(s.id)"><i class="ui at icon"></i>Reply</a></div>
        </div>
        <comment-group v-if="s.sons.length>0" v-bind:comments="s.sons" v-bind:level="1" v-on:reply="s.reply_action(s.id)"></comment-group>
    </div>
</div>`
});
/*
every comment should have:
{
    comments:[
        {
            fatherId: 1,  //or null
            normalPostTime:"",
            user:{},
            text:"",
            sons: [], //other comments
            reply_action: function (id) {
                ;//do something for replying
            }
        },
    ]
}

 */
