Meta:

Narrative:
As a user
I want to commit a transfer

Scenario: transfer
Given the first account with id 1 and balance 1000 and the second account with id 2 and balance 1000
When the user sends a transfer request with senderAccountId 1 and receiverAccountId 2 and amount 100
Then the transfer is committed
When user sends a getAccount request with accountId 1
Then account has id 1 and balance 900 returned
When user sends a getAccount request with accountId 2
Then account has id 2 and balance 1100 returned
