var pagination = Vue.component('pagination', {
    props: ['to_page', 'content'],
    template: `<a class="link item" v-html="content" v-on:click="$emit('change_page',to_page)"></a>`,
});