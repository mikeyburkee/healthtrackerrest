<template id="mood-profile">
  <app-layout>
    <div>
      <form v-if="mood">
        <label class="col-form-label">Mood ID: </label>
        <input class="form-control" v-model="mood.id" name="id" type="number" readonly/><br>
        <label class="col-form-label">User Id: </label>
        <input class="form-control" v-model="mood.userId" name="userId" type="number" readonly/><br>
        <label class="col-form-label">Description: </label>
        <input class="form-control" v-model="mood.description" name="description" type="text"/><br>
        <label class="col-form-label">Rating (0-10): </label>
        <input class="form-control" v-model="mood.rating" name="duration" type="number"/><br>
        <label class="col-form-label">Date: </label>
        <input class="form-control" v-model="mood.dateEntry" name="dateEntry" type="text"/><br>
      </form>
      <dt v-if="mood">
        <br>
        <a :href="`/users/${mood.userId}`">View User Profile</a>
      </dt>
    </div>
  </app-layout>
</template>

<script>
Vue.component("mood-profile", {
  template: "#mood-profile",
  data: () => ({
    mood: null
  }),
  created: function () {
    const moodId = this.$javalin.pathParams["mood-id"];
    const url = `/api/moods/${moodId}`
    axios.get(url)
        .then(res => this.mood = res.data)
        .catch(() => alert("Error while fetching mood" + moodId));
  }
});
</script>