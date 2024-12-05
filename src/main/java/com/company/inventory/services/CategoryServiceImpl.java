package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.model.Category;
import com.company.inventory.response.CategoryResponseRest;

@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private ICategoryDao categoryDao;
	
	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<CategoryResponseRest> search() {
		CategoryResponseRest response = new CategoryResponseRest();
		
		try {
			List<Category> category = (List<Category>) categoryDao.findAll();
			response.getCategoryResponse().setCategory(category);
			response.setMetadata("Respuesta ok", "00", "Respuesta exitosa");
		} catch (Exception e) {			
			response.setMetadata("Respuesta err", "-1", "Error al consultar");
			e.printStackTrace();
			return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<CategoryResponseRest> searchById(Long id) {
		CategoryResponseRest response = new CategoryResponseRest();
		List<Category> list = new ArrayList<>();
		
		try {
			Optional<Category> category = categoryDao.findById(id);
			if (category.isPresent()) {
				list.add(category.get());
				response.getCategoryResponse().setCategory(list);
				response.setMetadata("Respuesta ok", "00", "Categoria encontrada");
			} else {
				return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {			
			response.setMetadata("Respuesta err", "-1", "Error al consultar por id");
			e.printStackTrace();
			return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<CategoryResponseRest> save(Category category) {
	    CategoryResponseRest response = new CategoryResponseRest();
	    List<Category> list = new ArrayList<>();
	    
	    try {
	        Category categorySaved = categoryDao.save(category);
	        list.add(categorySaved);

	        response.setMetadata("Respuesta ok", "00", "Categoría guardada");
	        response.getCategoryResponse().setCategory(list);
	        return ResponseEntity.ok(response);
	        
	    } catch (IllegalArgumentException e) {
	        response.setMetadata("Respuesta err", "-1", "Datos inválidos para la categoría");
	        e.printStackTrace();
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        
	    } catch (Exception e) {
	        response.setMetadata("Respuesta err", "-1", "Error al guardar la categoría");
	        e.printStackTrace();
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	@Override
	@Transactional
	public ResponseEntity<CategoryResponseRest> update(Category category, Long id) {
	    CategoryResponseRest response = new CategoryResponseRest();
	    List<Category> list = new ArrayList<>();
	    
	    try {
	        Category categoryToUpdate = categoryDao.findById(id)
	                                              .orElseThrow(() -> new Exception("Categoría no encontrada"));

	        categoryToUpdate.setName(category.getName());
	        categoryToUpdate.setDescription(category.getDescription());
	        
	        Category updatedCategory = categoryDao.save(categoryToUpdate);
	        list.add(updatedCategory);
	        
	        response.setMetadata("Respuesta ok", "00", "Categoría actualizada");
	        response.getCategoryResponse().setCategory(list);
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        response.setMetadata("Respuesta err", "-1", e.getMessage().contains("Categoría no encontrada") ? 
	                               "Categoría no encontrada" : "Error al actualizar la categoría");
	        e.printStackTrace(); 
	        
	        HttpStatus status = e.getMessage().contains("Categoría no encontrada") ? 
	                            HttpStatus.NOT_FOUND : HttpStatus.CONFLICT;
	        return new ResponseEntity<>(response, status);
	    }
	}


	@Override
	@Transactional
	public ResponseEntity<CategoryResponseRest> deleteById(Long id) {
		CategoryResponseRest response = new CategoryResponseRest();
		
		try {
			if (categoryDao.existsById(id)) { 
				categoryDao.deleteById(id); 
				response.setMetadata("respuesta ok", "00", "Registro eliminado");
				return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.OK);
			} else { 
				response.setMetadata("Respuesta err", "-1", "Categoría no encontrada"); 
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); }
		
		} catch (Exception e) {			
			response.setMetadata("Respuesta err", "-1", "Error al eliminar");
			e.printStackTrace();
			return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}

}
