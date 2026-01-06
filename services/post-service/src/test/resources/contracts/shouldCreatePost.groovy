package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should create a post successfully"

    request {
        method POST()
        url "/api/v1/post/"
        headers {
            contentType applicationJson()
        }
        body([
                "body": "Integration test content",
                "tags": ["java", "testing"]
        ])
    }

    response {
        status 201

        headers {
            contentType applicationJson()
        }

        body([
                "postId": value(anyUuid()),

                // OffsetDateTime
                "createdAt": value(regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.+")),

                // OffsetDateTime
                "updatedAt": value(regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.+"))
        ])
    }
}