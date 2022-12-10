<template id="sleep-profile">
  <app-layout>
    <div>
      <form v-if="sleep">
        <label class="col-form-label">Sleep ID: </label>
        <input class="form-control" v-model="sleep.id" name="id" type="number" readonly/><br>
        <label class="col-form-label">User Id: </label>
        <input class="form-control" v-model="sleep.userId" name="userId" type="number" readonly/><br>
        <label class="col-form-label">Duration: </label>
        <input class="form-control" v-model="sleep.duration" name="duration" type="number"/><br>
        <label class="col-form-label">Rating (0-10): </label>
        <input class="form-control" v-model="sleep.rating" name="duration" type="number"/><br>
        <label class="col-form-label">Date: </label>
        <input class="form-control" v-model="sleep.wakeUpTime" name="wakeUpTime" type="text"/><br>
      </form>
      <dt v-if="sleep">
        <br>
        <a :href="`/users/${sleep.userId}`">View User Profile</a>
      </dt>
    </div>
  </app-layout>
</template>

<script>
Vue.component("sleep-profile", {
  template: "#sleep-profile",
  data: () => ({
    sleep: null
  }),
  created: function () {
    const sleepId = this.$javalin.pathParams["sleep-id"];
    const url = `/api/sleeps/${sleepId}`
    axios.get(url)
        .then(res => this.sleep = res.data)
        .catch(() => alert("Error while fetching sleep" + sleepId));
  }
});
</script>