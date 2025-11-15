package com.pet.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("medicines")
public class Medicine {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String category;
    private String description;
    
    @TableField("usage_instructions")
    private String usageInstructions;
    
    private String contraindications;
    
    private BigDecimal price;
    
    @TableField("stock_quantity")
    private Integer stockQuantity;
    
    private String unit;
    private String manufacturer;
    
    @TableField("expiry_date")
    private LocalDate expiryDate;
    
    @TableField("applicable_cat")
    private Integer applicableCat;
    
    @TableField("applicable_dog")
    private Integer applicableDog;
    
    @TableField("applicable_bird")
    private Integer applicableBird;
    
    @TableField("applicable_rabbit")
    private Integer applicableRabbit;
    
    @TableField("applicable_hamster")
    private Integer applicableHamster;
    
    private Integer status;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUsageInstructions() {
        return usageInstructions;
    }

    public void setUsageInstructions(String usageInstructions) {
        this.usageInstructions = usageInstructions;
    }

    public String getContraindications() {
        return contraindications;
    }

    public void setContraindications(String contraindications) {
        this.contraindications = contraindications;
    }

    public Integer getApplicableCat() {
        return applicableCat;
    }

    public void setApplicableCat(Integer applicableCat) {
        this.applicableCat = applicableCat;
    }

    public Integer getApplicableDog() {
        return applicableDog;
    }

    public void setApplicableDog(Integer applicableDog) {
        this.applicableDog = applicableDog;
    }

    public Integer getApplicableBird() {
        return applicableBird;
    }

    public void setApplicableBird(Integer applicableBird) {
        this.applicableBird = applicableBird;
    }

    public Integer getApplicableRabbit() {
        return applicableRabbit;
    }

    public void setApplicableRabbit(Integer applicableRabbit) {
        this.applicableRabbit = applicableRabbit;
    }

    public Integer getApplicableHamster() {
        return applicableHamster;
    }

    public void setApplicableHamster(Integer applicableHamster) {
        this.applicableHamster = applicableHamster;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
