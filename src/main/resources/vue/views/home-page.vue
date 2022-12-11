<!-- the "home-page" element is passed as a parameter to VueComponent in the JavalinConfig file -->
<template id="home-page">
  <app-layout>
    <div class="row">
      <div class="col">
        <div class="card">
          <h5 class="card-header">Registered Users</h5>
          <div class="card-body">
            <h5 class="card-title">{{users.length}} Registered</h5>
            <a href="/users" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Activities</h5>
          <div class="card-body">
            <h5 class="card-title">{{activities.length}} Logged</h5>
            <a href="/activities" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Daily Steps</h5>
          <div class="card-body">
            <h5 class="card-title">{{steps.length}} Logged</h5>
            <a href="/steps" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <div class="card">
          <h5 class="card-header">Daily Water</h5>
          <div class="card-body">
            <h5 class="card-title">{{waters.length}} Logged</h5>
            <a href="/waters" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Daily Mood</h5>
          <div class="card-body">
            <h5 class="card-title">{{moods.length}} Logged</h5>
            <a href="/moods" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Daily Sleep</h5>
          <div class="card-body">
            <h5 class="card-title">{{sleeps.length}} Logged</h5>
            <a href="/sleeps" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component('home-page',
    {
      template: "#home-page",
      data: () => ({
        users: [],
        activities: [],
        steps: [],
        sleeps: [],
        moods: [],
        waters: [],
      }),
      created() {
        axios.get("/api/users")
            .then(res => this.users = res.data)
            .catch(() => alert("Error while fetching users"));
        axios.get("/api/activities")
            .then(res => this.activities = res.data)
            .catch(() => alert("Error while fetching activities"));
        axios.get("/api/moods")
            .then(res => this.moods = res.data)
            .catch(() => alert("Error while fetching moods"));
        axios.get("/api/steps")
            .then(res => this.steps = res.data)
            .catch(() => alert("Error while fetching steps"));
        axios.get("/api/sleeps")
            .then(res => this.sleeps = res.data)
            .catch(() => alert("Error while fetching sleeps"));
        axios.get("/api/waters")
            .then(res => this.waters = res.data)
            .catch(() => alert("Error while fetching waters"));
      }
    });
</script>