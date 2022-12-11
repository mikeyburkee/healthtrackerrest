<template id="moods-overview">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(mood,index) in moods" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/moods/${mood.id}`"> Mood ID: {{mood.id }}  ( Description: {{ mood.description }}, Date: {{ mood.dateEntry }})</a></span>
        </div>
        <div class="p2">
          <a :href="`/moods/${mood.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                    @click="deleteMood(mood, index)">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </a>
        </div>
      </div>
    </div>
  </app-layout>
</template>
<script>
Vue.component("moods-overview", {
  template: "#moods-overview",
  data: () => ({
    moods: [],
  }),
  created() {
    this.fetchMoods();
  },
  methods: {
    fetchMoods: function () {
      axios.get("/api/moods")
          .then(res => this.moods = res.data)
          .catch(() => alert("Error while fetching moods"));
    }
  }
});
</script>