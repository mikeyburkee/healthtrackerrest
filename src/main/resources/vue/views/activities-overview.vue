<template id="activities-overview">
  <app-layout>
    <div>
      <div>
        <ul class="activities-overview-list">
          <li v-for="activity in activities">
            <a :href="`/activities/${activity.id}`"> Activity Id: {{activity.id}}
              ( Activity: {{activity.description}}, Duration: {{activity.duration}} minutes)</a>
          </li>
        </ul>
      </div>
    </div>
  </app-layout>
</template>
<script>
Vue.component("activities-overview", {
  template: "#activities-overview",
  data: () => ({
    activities: [],
  }),
  created() {
    this.fetchActivities();
  },
  methods: {
    fetchActivities: function () {
      axios.get("/api/activities")
          .then(res => this.activities = res.data)
          .catch(() => alert("Error while fetching activities"));
    }
  }
});
</script>