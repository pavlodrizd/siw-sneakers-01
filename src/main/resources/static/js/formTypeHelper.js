document.addEventListener('DOMContentLoaded', function () {
	        const existingTypeRadio = document.getElementById('existingTypeRadio');
	        const newTypeRadio = document.getElementById('newTypeRadio');
	        
	        const existingTypeBlock = document.getElementById('existingTypeBlock');
	        const newTypeBlock = document.getElementById('newTypeBlock');
	        
	        const typeSelect = document.getElementById('typeSelect');
	        const newTypeNameInput = document.getElementById('newTypeNameInput');

	        function toggleTypeFields() {
	            if (existingTypeRadio.checked) {
	                // Mostra il dropdown, nascondi l'input di testo
	                existingTypeBlock.style.display = 'block';
	                newTypeBlock.style.display = 'none';
	                
	                // Abilita il dropdown e disabilita l'input di testo per non inviarlo
	                typeSelect.disabled = false;
	                typeSelect.required = true;
	                newTypeNameInput.disabled = true;
	                newTypeNameInput.required = false;
	                
	            } else {
	                // Mostra l'input di testo, nascondi il dropdown
	                existingTypeBlock.style.display = 'none';
	                newTypeBlock.style.display = 'block';
	                
	                // Disabilita il dropdown e abilita l'input di testo
	                typeSelect.disabled = true;
	                typeSelect.required = false;
	                newTypeNameInput.disabled = false;
	                newTypeNameInput.required = true;
	            }
	        }

	        // Aggiungi gli ascoltatori per i click sui radio button
	        existingTypeRadio.addEventListener('change', toggleTypeFields);
	        newTypeRadio.addEventListener('change', toggleTypeFields);
	        
	        // Esegui la funzione all'inizio per impostare lo stato iniziale
	        toggleTypeFields();
	    });