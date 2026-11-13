package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Qué modificadores admite cada producto. Es una entidad y no un @ManyToMany porque la relación
 * tiene datos propios: max_selections y required.
 */
@Entity
@Table(name = "menu_item_modifiers", uniqueConstraints = @UniqueConstraint(name = "uk_menu_item_modifiers_item_modifier", columnNames = {
		"menuitems_id", "modificador_id" }))
public class MenuItemModifierEntity {

	@Id
	@Column(name = "menu_item_modifier_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menu_item_modifier_id_generator")
	@SequenceGenerator(name = "menu_item_modifier_id_generator", sequenceName = "menu_item_modifier_id_seq", allocationSize = 1)
	private Long menuItemModifierId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menuitems_id", nullable = false)
	private MenuItemEntity menuItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "modificador_id", nullable = false)
	private ModifierEntity modifier;

	@Column(name = "max_selections", nullable = false)
	private Integer maxSelections = 1;

	@Column(nullable = false)
	private Boolean required = false;

	public MenuItemModifierEntity() {
		super();
	}

	public Long getMenuItemModifierId() {
		return menuItemModifierId;
	}

	public void setMenuItemModifierId(Long menuItemModifierId) {
		this.menuItemModifierId = menuItemModifierId;
	}

	public MenuItemEntity getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItemEntity menuItem) {
		this.menuItem = menuItem;
	}

	public ModifierEntity getModifier() {
		return modifier;
	}

	public void setModifier(ModifierEntity modifier) {
		this.modifier = modifier;
	}

	public Integer getMaxSelections() {
		return maxSelections;
	}

	public void setMaxSelections(Integer maxSelections) {
		this.maxSelections = maxSelections;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

}
