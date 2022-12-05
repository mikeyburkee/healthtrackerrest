<template id="activity-profile">
  <app-layout>
    <div>
      <form v-if="activity">
        <label class="col-form-label">Activity ID: </label>
        <input class="form-control" v-model="activity.id" name="id" type="number" readonly/><br>
        <label class="col-form-label">User Id: </label>
        <input class="form-control" v-model="activity.userId" name="userId" type="number" readonly/><br>
        <label class="col-form-label">Name: </label>
        <input class="form-control" v-model="activity.description" name="activitydescription" type="text"/><br>
        <label class="col-form-label">Duration (minutes): </label>
        <input class="form-control" v-model="activity.duration" name="duration" type="number"/><br>
        <label class="col-form-label">Calories: </label>
        <input class="form-control" v-model="activity.calories" name="calories" type="number"/><br>
        <label class="col-form-label">Rating (0-10): </label>
        <input class="form-control" v-model="activity.rating" name="rating" type="number"/><br>
        <label class="col-form-label">Date: </label>
        <input class="form-control" v-model="activity.started" name="started" type="text"/><br>
      </form>
      <dt v-if="activity">
        <br>
        <a :href="`/users/${activity.userId}`">View User Profile</a>
      </dt>
    </div>
  </app-layout>
</template>

<script>
Vue.component("activity-profile", {
  template: "#activity-profile",
  data: () => ({
    activity: null
  }),
  created: function () {
    const activityId = this.$javalin.pathParams["activity-id"];
    const url = `/api/activities/${activityId}`
    axios.get(url)
        .then(res => this.activity = res.data)
        .catch(() => alert("Error while fetching activity" + activityId));
  }
});
</script>