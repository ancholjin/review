package org.zerock.review.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.review.dto.PhotoDTO;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
public class UploadController {

    @PostMapping(value = "/uploadAjax", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PhotoDTO>> uploadFile(MultipartFile[] uploadFiles){
        //반환 값은 yyyy/MM/dd
        String uploadPath = makeFolder();

        List<PhotoDTO> list = new ArrayList<>();

        for (MultipartFile uploadFile: uploadFiles) {

            //이미지 파일만 업로드 할 수 있도록 보호
            if(uploadFile.getContentType().startsWith("image") == false) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            //실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로
            String originalName = uploadFile.getOriginalFilename();
            String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

            log.info("fileName: " + fileName);

            //동일한 파일 이름을 구분하기 위한 고유 식별자
            UUID uuid = UUID.randomUUID();
            fileName = uuid.toString()+"_"+ fileName;
            File saveFile = new File("C:\\upload" + File.separator+uploadPath, fileName);

            try {
                uploadFile.transferTo(saveFile);
                //섬네일 파일 이름은 중간에 s_로 시작하도록
                File thumbnailFile = new File("C:\\upload" + File.separator+uploadPath,"s_"+fileName );
                //섬네일 생성
                Thumbnailator.createThumbnail(saveFile, thumbnailFile,100,100);
                //결과 데이터 처리
                list.add(PhotoDTO.builder()
                        .uploadPath(uploadPath)
                        .fileName(originalName.substring(originalName.lastIndexOf("\\") + 1))
                        .uuid(uuid.toString()).build());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }//end for
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    private String makeFolder() {

        String uploadFolder = "C:\\upload";
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));


        String folderPath =  str.replace("//", File.separator);

        // make folder --------
        File uploadPathFolder = new File(uploadFolder, folderPath);

        if (uploadPathFolder.exists() == false) {
            uploadPathFolder.mkdirs();
        }
        return folderPath;
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName) {

        ResponseEntity<byte[]> result = null;

        try {
            log.info("fileName: " + URLDecoder.decode(fileName,"UTF-8"));

            File file = new File("c:\\upload\\" + URLDecoder.decode(fileName,"UTF-8"));

            log.info("file: " + file);

            HttpHeaders header = new HttpHeaders();

            header.add("Content-Type", Files.probeContentType(file.toPath()));
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @PostMapping("/deleteFile")
    public ResponseEntity<String> deleteFile(String fileName) {

        log.info("deleteFile: " + fileName);

        try {
            File file = new File("c:\\upload\\" + URLDecoder.decode(fileName, "UTF-8"));

            file.delete();

            String largeFileName = file.getAbsolutePath().replace("s_", "");

            log.info("largeFileName: " + largeFileName);

            file = new File(largeFileName);

            file.delete();

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>("deleted", HttpStatus.OK);

    }
}
