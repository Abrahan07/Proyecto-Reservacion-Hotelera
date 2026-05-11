package com.universidad.staytic.model;

import java.util.Date;

    public class Maintenance {

        private int maintenanceId;
        private Room room;
        private String type;
        private String description;
        private Date scheduledDate;
        private Date completedDate;
        private String status;
        private User employee;

        public Maintenance() {}

        public Maintenance(int maintenanceId, Room room, String type,
                             String description, Date scheduledDate, Date completedDate,
                             String status, User employee) {
            this.maintenanceId = maintenanceId;
            this.room = room;
            this.type = type;
            this.description = description;
            this.scheduledDate = scheduledDate;
            this.completedDate = completedDate;
            this.status = status;
            this.employee = employee;
        }

        // maintenanceId: solo get, el id no cambia
        public int getMaintenanceId() { return maintenanceId; }

        public Room getRoom() { return room; }
        public void setRoom(Room room) { this.room = room; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Date getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }

        // completedDate: solo get, se asigna al llamar complete()
        public Date getCompletedDate() { return completedDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public User getEmployee() { return employee; }
        public void setEmployee(User employee) { this.employee = employee; }

        @Override
        public String toString() {
            return "Maintenance{maintenanceId=" + maintenanceId + ", room=" + room +
                    ", type='" + type + "', status='" + status + "', scheduledDate=" +
                    scheduledDate + "}";
        }
    }
