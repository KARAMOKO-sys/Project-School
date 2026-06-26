package com.edueasy.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "addresses"
)
public class Address {
    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(
            name = "id",
            length = 36
    )
    private String id;
    @Column(
            name = "street",
            length = 255
    )
    private String street;
    @Column(
            name = "city",
            length = 100
    )
    private String city;
    @Column(
            name = "state",
            length = 100
    )
    private String state;
    @Column(
            name = "country",
            length = 100
    )
    private String country;
    @Column(
            name = "postal_code",
            length = 20
    )
    private String postalCode;
    @Column(
            name = "latitude"
    )
    private Double latitude;
    @Column(
            name = "longitude"
    )
    private Double longitude;
    @Column(
            name = "is_primary"
    )
    private boolean isPrimary = false;

    protected Address(final AddressBuilder<?, ?> b) {
        this.id = b.id;
        this.street = b.street;
        this.city = b.city;
        this.state = b.state;
        this.country = b.country;
        this.postalCode = b.postalCode;
        this.latitude = b.latitude;
        this.longitude = b.longitude;
        this.isPrimary = b.isPrimary;
    }

    public static AddressBuilder<?, ?> builder() {
        return new AddressBuilderImpl();
    }

    public String getId() {
        return this.id;
    }

    public String getStreet() {
        return this.street;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public String getCountry() {
        return this.country;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public boolean isPrimary() {
        return this.isPrimary;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    public void setPrimary(final boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Address)) {
            return false;
        } else {
            Address other = (Address)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isPrimary() != other.isPrimary()) {
                return false;
            } else {
                Object this$latitude = this.getLatitude();
                Object other$latitude = other.getLatitude();
                if (this$latitude == null) {
                    if (other$latitude != null) {
                        return false;
                    }
                } else if (!this$latitude.equals(other$latitude)) {
                    return false;
                }

                Object this$longitude = this.getLongitude();
                Object other$longitude = other.getLongitude();
                if (this$longitude == null) {
                    if (other$longitude != null) {
                        return false;
                    }
                } else if (!this$longitude.equals(other$longitude)) {
                    return false;
                }

                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$street = this.getStreet();
                Object other$street = other.getStreet();
                if (this$street == null) {
                    if (other$street != null) {
                        return false;
                    }
                } else if (!this$street.equals(other$street)) {
                    return false;
                }

                Object this$city = this.getCity();
                Object other$city = other.getCity();
                if (this$city == null) {
                    if (other$city != null) {
                        return false;
                    }
                } else if (!this$city.equals(other$city)) {
                    return false;
                }

                Object this$state = this.getState();
                Object other$state = other.getState();
                if (this$state == null) {
                    if (other$state != null) {
                        return false;
                    }
                } else if (!this$state.equals(other$state)) {
                    return false;
                }

                Object this$country = this.getCountry();
                Object other$country = other.getCountry();
                if (this$country == null) {
                    if (other$country != null) {
                        return false;
                    }
                } else if (!this$country.equals(other$country)) {
                    return false;
                }

                Object this$postalCode = this.getPostalCode();
                Object other$postalCode = other.getPostalCode();
                if (this$postalCode == null) {
                    if (other$postalCode != null) {
                        return false;
                    }
                } else if (!this$postalCode.equals(other$postalCode)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Address;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isPrimary() ? 79 : 97);
        Object $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : $latitude.hashCode());
        Object $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : $longitude.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $street = this.getStreet();
        result = result * 59 + ($street == null ? 43 : $street.hashCode());
        Object $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        Object $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        Object $country = this.getCountry();
        result = result * 59 + ($country == null ? 43 : $country.hashCode());
        Object $postalCode = this.getPostalCode();
        result = result * 59 + ($postalCode == null ? 43 : $postalCode.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getId();
        return "Address(id=" + var10000 + ", street=" + this.getStreet() + ", city=" + this.getCity() + ", state=" + this.getState() + ", country=" + this.getCountry() + ", postalCode=" + this.getPostalCode() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ", isPrimary=" + this.isPrimary() + ")";
    }

    public Address() {
    }

    public Address(final String id, final String street, final String city, final String state, final String country, final String postalCode, final Double latitude, final Double longitude, final boolean isPrimary) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isPrimary = isPrimary;
    }

    public abstract static class AddressBuilder<C extends Address, B extends AddressBuilder<C, B>> {
        private String id;
        private String street;
        private String city;
        private String state;
        private String country;
        private String postalCode;
        private Double latitude;
        private Double longitude;
        private boolean isPrimary;

        public AddressBuilder() {
        }

        public B id(final String id) {
            this.id = id;
            return (B)this.self();
        }

        public B street(final String street) {
            this.street = street;
            return (B)this.self();
        }

        public B city(final String city) {
            this.city = city;
            return (B)this.self();
        }

        public B state(final String state) {
            this.state = state;
            return (B)this.self();
        }

        public B country(final String country) {
            this.country = country;
            return (B)this.self();
        }

        public B postalCode(final String postalCode) {
            this.postalCode = postalCode;
            return (B)this.self();
        }

        public B latitude(final Double latitude) {
            this.latitude = latitude;
            return (B)this.self();
        }

        public B longitude(final Double longitude) {
            this.longitude = longitude;
            return (B)this.self();
        }

        public B isPrimary(final boolean isPrimary) {
            this.isPrimary = isPrimary;
            return (B)this.self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "Address.AddressBuilder(id=" + this.id + ", street=" + this.street + ", city=" + this.city + ", state=" + this.state + ", country=" + this.country + ", postalCode=" + this.postalCode + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ", isPrimary=" + this.isPrimary + ")";
        }
    }

    private static final class AddressBuilderImpl extends AddressBuilder<Address, AddressBuilderImpl> {
        private AddressBuilderImpl() {
        }

        protected AddressBuilderImpl self() {
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}
