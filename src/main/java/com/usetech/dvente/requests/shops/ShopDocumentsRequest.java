package com.usetech.dvente.requests.shops;

import com.usetech.dvente.requests.files.ValidDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDocumentsRequest {

    @ValidDocument
    private MultipartFile ifuDocument;

    @ValidDocument
    private MultipartFile rcmDocument;
}