public enum RoomCategory {
    STANDARD(2000, "Comfortable room with a double bed, TV and Wi-Fi"),
    DELUXE(3500, "Spacious room with a king bed, balcony and mini-bar"),
    SUITE(6000, "Luxury suite with living area, bathtub and city view");
    private final double pricePerNight;
    private final String description;
    RoomCategory(double pricePerNight, String description) {
        this.pricePerNight = pricePerNight;
        this.description = description;
    }
    public double getPricePerNight() { return pricePerNight; }
    public String getDescription() { return description; }
}
