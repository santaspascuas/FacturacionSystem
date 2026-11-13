package com.facturacionAutonomo.product_service.domain.model;

import com.facturacionAutonomo.product_service.domain.exception.ModifierException;

/**
 * Un modificador que admite un producto, con sus reglas: cuántas veces se puede elegir y si es obligatorio.
 * El producto y el modificador tienen que ser del mismo tenant; lo comprueba el servicio antes de crearlo.
 */
public class MenuItemModifier {
	private final Long id;
	private final Long menuItemId;
	private final Long modifierId;
	private int maxSelections;
	private boolean required;


	private MenuItemModifier(Long id, Long menuItemId, Long modifierId, int maxSelections, boolean required) {
		super();
		this.id = id;
		this.menuItemId = menuItemId;
		this.modifierId = modifierId;
		this.maxSelections = maxSelections;
		this.required = required;
	}


	public static MenuItemModifier crearMenuItemModifier(Long menuItemId, Long modifierId, int maxSelections,
			boolean required) {
		return new MenuItemModifier(null, menuItemId, modifierId, maxSelectionsValido(maxSelections), required);
	}

	public static MenuItemModifier restoreData(Long id, Long menuItemId, Long modifierId, int maxSelections,
			boolean required) {
		return new MenuItemModifier(id, menuItemId, modifierId, maxSelections, required);
	}

	public void updateRules(int maxSelections, boolean required) {
		this.maxSelections = maxSelectionsValido(maxSelections);
		this.required = required;
	}

	// Coincide con el CHECK (max_selections >= 1) de menu_item_modifiers.
	private static int maxSelectionsValido(int maxSelections) {
		if(maxSelections < 1) {
			throw new ModifierException("El número máximo de selecciones tiene que ser al menos 1");
		}
		return maxSelections;
	}


	public Long getId() {
		return id;
	}
	public Long getMenuItemId() {
		return menuItemId;
	}
	public Long getModifierId() {
		return modifierId;
	}
	public int getMaxSelections() {
		return maxSelections;
	}
	public boolean isRequired() {
		return required;
	}
	@Override
	public String toString() {
		return "MenuItemModifier [id=" + id + ", menuItemId=" + menuItemId + ", modifierId=" + modifierId
				+ ", maxSelections=" + maxSelections + ", required=" + required + "]";
	}

}
