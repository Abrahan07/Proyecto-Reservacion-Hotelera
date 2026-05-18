package com.universidad.staytic.service;

import com.universidad.staytic.model.*;
import com.universidad.staytic.repository.MaintenanceRepository;
import com.universidad.staytic.repository.PaymentRepository;
import com.universidad.staytic.repository.ReservationRepository;
import com.universidad.staytic.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final MaintenanceRepository maintenanceRepository;

    public AdminDashboardService(PaymentRepository paymentRepository,
                                 ReservationRepository reservationRepository,
                                 RoomRepository roomRepository,
                                 MaintenanceRepository maintenanceRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    public AdminDashboardStats buildStats() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate weekStart = today.minusDays(6);

        List<Payment> monthPayments = paymentRepository.findByPaymentDateBetween(monthStart, today);
        List<Payment> weekPayments = paymentRepository.findByPaymentDateBetween(weekStart, today);
        List<Room> rooms = roomRepository.findAll();

        float monthlyRevenue = sumPayments(monthPayments);
        List<RevenueDay> revenueDays = buildRevenueDays(weekStart, weekPayments);
        float weeklyRevenue = revenueDays.stream().map(RevenueDay::getAmount).reduce(0f, Float::sum);
        float dailyAverage = revenueDays.isEmpty() ? 0 : weeklyRevenue / revenueDays.size();

        long totalRooms = rooms.size();
        long occupiedRooms = rooms.stream().filter(room -> room.getStatus() == RoomStatus.OCCUPIED).count();
        long availableRooms = roomRepository.countByStatus(RoomStatus.AVAILABLE);
        long maintenanceRooms = roomRepository.countByStatus(RoomStatus.MAINTENANCE);
        int occupancyPercent = totalRooms == 0 ? 0 : Math.round((occupiedRooms * 100f) / totalRooms);

        return new AdminDashboardStats(
                occupancyPercent,
                monthlyRevenue,
                reservationRepository.countByStatusIn(List.of(ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE)),
                availableRooms,
                maintenanceRooms,
                dailyAverage,
                weeklyRevenue,
                roomTypeOccupancy(rooms),
                revenueDays,
                reservationRepository.findTop5ByOrderByReservationIdDesc(),
                maintenanceRepository.findTop2ByStatusInOrderByScheduledDateAscMaintenanceIdDesc(
                        List.of(MaintenanceStatus.REPORTED, MaintenanceStatus.SCHEDULED))
        );
    }

    private List<RevenueDay> buildRevenueDays(LocalDate weekStart, List<Payment> payments) {
        Map<LocalDate, Float> revenueByDate = payments.stream()
                .collect(Collectors.groupingBy(Payment::getPaymentDate, Collectors.reducing(0f, Payment::getAmount, Float::sum)));
        float max = Math.max(1f, revenueByDate.values().stream().max(Float::compare).orElse(0f));
        List<RevenueDay> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            float amount = revenueByDate.getOrDefault(date, 0f);
            days.add(new RevenueDay(dayLabel(date.getDayOfWeek()), amount, Math.max(8, Math.round((amount / max) * 100))));
        }
        return days;
    }

    private List<RoomTypeOccupancy> roomTypeOccupancy(List<Room> rooms) {
        List<RoomTypeOccupancy> result = new ArrayList<>();
        for (RoomType type : RoomType.values()) {
            List<Room> byType = rooms.stream().filter(room -> room.getType() == type).toList();
            long occupied = byType.stream().filter(room -> room.getStatus() == RoomStatus.OCCUPIED).count();
            int percent = byType.isEmpty() ? 0 : Math.round((occupied * 100f) / byType.size());
            result.add(new RoomTypeOccupancy(displayRoomType(type), percent));
        }
        return result;
    }

    private float sumPayments(List<Payment> payments) {
        return payments.stream().map(Payment::getAmount).reduce(0f, Float::sum);
    }

    private String displayRoomType(RoomType type) {
        return switch (type) {
            case SINGLE -> "Simple";
            case DOUBLE -> "Doble";
            case SUITE -> "Suite";
            case FAMILY -> "Familiar";
        };
    }

    private String dayLabel(DayOfWeek day) {
        return day.getDisplayName(TextStyle.SHORT, new Locale("es", "CO")).substring(0, 1).toUpperCase();
    }

    public static class AdminDashboardStats {
        private final int occupancyPercent;
        private final float monthlyRevenue;
        private final long activeReservations;
        private final long availableRooms;
        private final long maintenanceRooms;
        private final float dailyAverageRevenue;
        private final float weeklyRevenue;
        private final List<RoomTypeOccupancy> roomTypeOccupancy;
        private final List<RevenueDay> revenueDays;
        private final List<Reservation> recentReservations;
        private final List<Maintenance> pendingMaintenances;

        public AdminDashboardStats(int occupancyPercent, float monthlyRevenue, long activeReservations,
                                   long availableRooms, long maintenanceRooms, float dailyAverageRevenue,
                                   float weeklyRevenue, List<RoomTypeOccupancy> roomTypeOccupancy,
                                   List<RevenueDay> revenueDays, List<Reservation> recentReservations,
                                   List<Maintenance> pendingMaintenances) {
            this.occupancyPercent = occupancyPercent;
            this.monthlyRevenue = monthlyRevenue;
            this.activeReservations = activeReservations;
            this.availableRooms = availableRooms;
            this.maintenanceRooms = maintenanceRooms;
            this.dailyAverageRevenue = dailyAverageRevenue;
            this.weeklyRevenue = weeklyRevenue;
            this.roomTypeOccupancy = roomTypeOccupancy;
            this.revenueDays = revenueDays;
            this.recentReservations = recentReservations;
            this.pendingMaintenances = pendingMaintenances;
        }

        public int getOccupancyPercent() { return occupancyPercent; }
        public float getMonthlyRevenue() { return monthlyRevenue; }
        public long getActiveReservations() { return activeReservations; }
        public long getAvailableRooms() { return availableRooms; }
        public long getMaintenanceRooms() { return maintenanceRooms; }
        public float getDailyAverageRevenue() { return dailyAverageRevenue; }
        public float getWeeklyRevenue() { return weeklyRevenue; }
        public List<RoomTypeOccupancy> getRoomTypeOccupancy() { return roomTypeOccupancy; }
        public List<RevenueDay> getRevenueDays() { return revenueDays; }
        public List<Reservation> getRecentReservations() { return recentReservations; }
        public List<Maintenance> getPendingMaintenances() { return pendingMaintenances; }
    }

    public static class RoomTypeOccupancy {
        private final String type;
        private final int percent;

        public RoomTypeOccupancy(String type, int percent) {
            this.type = type;
            this.percent = percent;
        }

        public String getType() { return type; }
        public int getPercent() { return percent; }
    }

    public static class RevenueDay {
        private final String label;
        private final float amount;
        private final int percent;

        public RevenueDay(String label, float amount, int percent) {
            this.label = label;
            this.amount = amount;
            this.percent = percent;
        }

        public String getLabel() { return label; }
        public float getAmount() { return amount; }
        public int getPercent() { return percent; }
    }
}
