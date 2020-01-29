Vue.component("header-tab",{
    props:["loc","cid"],
    template :`<div class="ui tabular menu ">
        <a :href="'../problem/'+cid" class="item" v-if="loc!='problem'">Problem</a>
        <div class="active item" v-if="loc=='problem'">Problem</div>
        <a :href="'../status/'+cid" class="item" v-if="loc!='statu'">Status</a>
        <div class="active item" v-if="loc=='statu'">Status</div>
        <a :href="'../ranklist/'+cid" class="item" v-if="loc!='ranklist'">Ranklist</a>
        <div class="active item" v-if="loc=='ranklist'">Ranklist</div>
        <a :href="'../comment/'+cid" class="item" v-if="loc!='comment'">Q&A</a>
        <div class="active item" v-if="loc=='comment'">Q&A</div>
    </div>`
})