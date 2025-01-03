package org.example.mobile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mobile.repository.MobileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mobiles")
public class MobileController {

    @Autowired
    private MobileRepository mobileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public List<Mobile> getAllMobiles() {
        return mobileRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mobile> getMobileById(@PathVariable Long id) {
        Optional<Mobile> mobile = mobileRepository.findById(id);
        if (mobile.isPresent()) {
            return ResponseEntity.ok(mobile.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Mobile> createMobile(@RequestParam("mobile") String mobileStr, @RequestParam("image") MultipartFile file) {
        try {
            JsonNode jsonNode = objectMapper.readTree(mobileStr);
            Mobile mobile = objectMapper.treeToValue(jsonNode, Mobile.class);
            mobile.setImage(file.getBytes());
            Mobile savedMobile = mobileRepository.save(mobile);
            return new ResponseEntity<>(savedMobile, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Mobile> updateMobile(
            @PathVariable Long id,
            @RequestParam("mobile") String mobileStr,
            @RequestParam(value = "image", required = false) MultipartFile file) {
        try {
            Optional<Mobile> mobileData = mobileRepository.findById(id);

            if (!mobileData.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Mobile existingMobile = mobileData.get();
            JsonNode jsonNode = objectMapper.readTree(mobileStr);
            Mobile updatedDetails = objectMapper.treeToValue(jsonNode, Mobile.class);

            existingMobile.setBrand(updatedDetails.getBrand());
            existingMobile.setModel(updatedDetails.getModel());
            existingMobile.setPrice(updatedDetails.getPrice());
            existingMobile.setCurrency(updatedDetails.getCurrency());
            existingMobile.setStock(updatedDetails.getStock());

            if (file != null && !file.isEmpty()) {
                existingMobile.setImage(file.getBytes());
            }

            Mobile updatedMobile = mobileRepository.save(existingMobile);
            return new ResponseEntity<>(updatedMobile, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteMobile(@PathVariable Long id) {
        try {
            mobileRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<HttpStatus> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        try {
            Mobile mobile = mobileRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
            mobile.setImage(file.getBytes());
            mobileRepository.save(mobile);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/deleteAll")
    public ResponseEntity<HttpStatus> deleteAllMobiles() {
        try {
            mobileRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Successfully deleted everything
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Something went wrong
        }
    }

}
