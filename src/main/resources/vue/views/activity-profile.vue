<template id="activity-profile">
  <app-layout>
    <div v-if="noActivityFound">
      <p> We're sorry, we were not able to retrieve this activity.</p>
      <p> View <a :href="'/activities'">all activities</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noActivityFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Activity Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateActivity()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteActivity()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
    <div class="card-body">
      <form>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-activity-id">Activity ID</span>
          </div>
          <input type="number" class="form-control" v-model="activity.id" name="activity-id" readonly placeholder="ActivityId"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-activity-id">User ID</span>
          </div>
          <input type="number" class="form-control" v-model="activity.userId" name="activity-userid" readonly placeholder="UserID"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-activity-description">Description</span>
          </div>
          <input type="text" class="form-control" v-model="activity.description" name="activity-description"  placeholder="Description"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-activity-duration">Duration</span>
          </div>
          <input type="number" class="form-control" v-model="activity.duration" name="activity-duration"  placeholder="Duration"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-activity-rating">Rating</span>
          </div>
          <input type="number" class="form-control" v-model="activity.rating" name="activity-rating"  placeholder="Rating"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-activity-calories">Calories</span>
          </div>
          <input type="number" class="form-control" v-model="activity.calories" name="activity-calories"  placeholder="Calories"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-activity-date">Date Started</span>
          </div>
          <input type="text" class="form-control" v-model="activity.started" name="activity-started"  placeholder="Started"/>
        </div>
      </form>
    </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("activity-profile", {
  template: "#activity-profile",
  data: () => ({
    activity: null,
    noActivityFound: false,
  }),
  created: function () {
    const activityId = this.$javalin.pathParams["activity-id"];
    const url = `/api/activities/${activityId}`
    axios.get(url)
        .then(res => this.activity = res.data)
        .catch(error => {
          console.log("No activity found for id passed in the path parameter: " + error)
          this.noActivityFound = true
        })
  },
  methods: {
    updateActivity: function () {
      const activityId = this.$javalin.pathParams["activity-id"];
      const url = `/api/activities/${activityId}`
      axios.patch(url,
          {
            userId: this.activity.userId,
            description: this.activity.description,
            duration: this.activity.duration,
            rating: this.activity.rating,
            calories: this.activity.calories,
            started: this.activity.started
          })
          .then(response =>
              this.activity.push(response.data))
          .catch(error => {
            console.log(error)
          })
      alert("Activity updated!")
    },
    deleteActivity: function () {
      if (confirm("Do you really want to delete?")) {
        const activityId = this.$javalin.pathParams["activity-id"];
        const url = `/api/activities/${activityId}`
        axios.delete(url)
            .then(response => {
              alert("Activity deleted")
              //display the /activities endpoint
              window.location.href = '/activities';
            })
            .catch(function (error) {
              console.log(error)
            });
      }
    }
  }
});
</script>