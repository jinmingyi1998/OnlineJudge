Vue.component('submit-button', {
    data() {
        return {isdisabled: false}
    },
    template: `<a class="ui inverted primary button" v-bind:class="{disabled:isdisabled,loading:isdisabled}" v-on:click="click_btn">提交</a>`,
    methods: {
        click_btn: function () {
            this.isdisabled = true;
            var that = this;
            setTimeout(function () {
                that.isdisabled = false;
            }, 2000);
            prom.submit();
        }
    }
});